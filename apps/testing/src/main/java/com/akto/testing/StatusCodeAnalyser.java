package com.akto.testing;

import com.akto.dto.ApiInfo;
import com.akto.dto.HttpRequestParams;
import com.akto.dto.HttpResponseParams;
import com.akto.dto.testing.AuthMechanism;
import com.akto.store.AuthMechanismStore;
import com.akto.store.SampleMessageStore;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

import static com.akto.runtime.RelationshipSync.extractAllValuesFromPayload;

public class StatusCodeAnalyser {

    static ObjectMapper mapper = new ObjectMapper();
    static JsonFactory factory = mapper.getFactory();

    static List<StatusCodeIdentifier> result = new ArrayList<>();

    static {
        // TODO: remove this
        result.add(new StatusCodeIdentifier(new HashSet<>(Arrays.asList("status#code", "status#reason", "status#message", "status#type", "status#title")), "status#code"));
    }

    public static class StatusCodeIdentifier {
        public Set<String> keySet;
        public String statusCodeKey;

        public StatusCodeIdentifier(Set<String> keySet, String statusCodeKey) {
            this.keySet = keySet;
            this.statusCodeKey = statusCodeKey;
        }

        @Override
        public String toString() {
            return keySet + "  " + statusCodeKey;
        }
    }


    public static void run() {
        Map<ApiInfo.ApiInfoKey, List<String>> sampleDataMap = SampleMessageStore.sampleDataMap;
        int count = 0;
        AuthMechanism authMechanism = AuthMechanismStore.getAuthMechanism();
        if (authMechanism == null) return;
        Map<Set<String>, Map<String,Integer>> frequencyMap = new HashMap<>();

        for (ApiInfo.ApiInfoKey apiInfoKey: sampleDataMap.keySet()) {
            if (count > 30) break;

            // fetch sample message
            HttpResponseParams originalHttpResponseParams = SampleMessageStore.fetchOriginalMessage(apiInfoKey);
            if (originalHttpResponseParams == null) continue;
            // discard if payload is null or empty
            if (originalHttpResponseParams.getPayload() == null || originalHttpResponseParams.getPayload().equals("{}")) {
                continue;
            }

            // if auth token is not passed originally -> skip
            HttpRequestParams httpRequestParams = originalHttpResponseParams.getRequestParams();
            boolean result = authMechanism.removeAuthFromRequest(httpRequestParams);
            if (!result) continue;

            // execute API
            HttpResponseParams httpResponseParams;
            try {
                 httpResponseParams = ApiExecutor.sendRequest(httpRequestParams);
            } catch (Exception e) {
                continue;
            }

            // if non 2xx then skip this api
            if (httpResponseParams.statusCode < 200 || httpResponseParams.statusCode >= 300) continue;

            // store response keys and values in map
            String payload = httpResponseParams.getPayload();
            String originalPayload = originalHttpResponseParams.getPayload();
            Map<String, Set<String>> responseParamMap = new HashMap<>();
            Map<String, Set<String>> originalResponseParamMap = new HashMap<>();
            try {
                JsonParser jp = factory.createParser(payload);
                JsonNode node = mapper.readTree(jp);
                extractAllValuesFromPayload(node,new ArrayList<>(), responseParamMap);

                jp = factory.createParser(originalPayload);
                node = mapper.readTree(jp);
                extractAllValuesFromPayload(node,new ArrayList<>(), originalResponseParamMap);
            } catch (Exception e) {
                continue;
            }

            if (responseParamMap.size() > 10) continue;

            List<String> potentialStatusCodeKeys = new ArrayList<>();
            for (String key: responseParamMap.keySet()) {
                Set<String> val = responseParamMap.get(key);
                if (val.size() != 1) continue;
                List<String> valList = new ArrayList<>(val);
                String statusCodeString = valList.get(0);
                try {
                    int statusCode = Integer.parseInt(statusCodeString);
                    if (statusCode < 0 || statusCode > 999 ) continue;
                } catch (Exception e) {
                    continue;
                }
                potentialStatusCodeKeys.add(key);
                break;
            }

            if (potentialStatusCodeKeys.isEmpty()) continue;

            Set<String> params = responseParamMap.keySet();
            Map<String, Integer> newCountObj = frequencyMap.get(params);
            if (newCountObj != null) {
                for (String statusCodeKey: potentialStatusCodeKeys) {
                    Integer val = newCountObj.getOrDefault(statusCodeKey, 0);
                    newCountObj.put(statusCodeKey, val + 1);
                }
            } else {
                // Add only if original and current payloads are different
                if (originalResponseParamMap.keySet().equals(responseParamMap.keySet())) {
                    continue;
                }
                frequencyMap.put(params, new HashMap<>());
                for (String statusCodeKey: potentialStatusCodeKeys) {
                    frequencyMap.get(params).put(statusCodeKey, 1);
                }
            }


            count += 1;
        }

        int threshold = 5;
        for (Set<String> params: frequencyMap.keySet()) {
            Map<String, Integer> countObj = frequencyMap.get(params);
            for (String key: countObj.keySet()) {
                if (countObj.get(key) > threshold) {
                    result.add(new StatusCodeIdentifier(params, key));
                    break;
                }
            }
        }

        System.out.println("*********************");
        System.out.println(result);
        System.out.println("*********************");
    }


    public static int getStatusCode(HttpResponseParams httpResponseParams) {
        int statusCode = httpResponseParams.getStatusCode();
        String payload = httpResponseParams.getPayload();
        if (statusCode < 200 || statusCode >= 300) return statusCode;

        Map<String, Set<String>> responseParamMap = new HashMap<>();
        try {
            JsonParser jp = factory.createParser(payload);
            JsonNode node = mapper.readTree(jp);
            extractAllValuesFromPayload(node,new ArrayList<>(), responseParamMap);
        } catch (Exception e) {
            return statusCode;
        }

        for (StatusCodeIdentifier statusCodeIdentifier: result) {
            boolean flag = false;
            for (String key: statusCodeIdentifier.keySet) {
                if (!responseParamMap.containsKey(key)) {
                    flag = true;
                    break;
                }
            }

            if (flag) continue;

            Set<String> val = responseParamMap.get(statusCodeIdentifier.statusCodeKey);
            if (val == null || val.isEmpty()) continue;
            String vv = val.iterator().next();
            try {
                return Integer.parseInt(vv);
            } catch (Exception ignored) { }
        }

        return statusCode;
    }

}
