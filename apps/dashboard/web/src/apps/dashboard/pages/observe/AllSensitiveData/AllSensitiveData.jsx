import PageWithMultipleCards from "../../../components/layouts/PageWithMultipleCards"
import { Text, Button } from "@shopify/polaris"
import api from "../api"
import { useEffect,useState } from "react"
import func from "@/util/func"
import GithubSimpleTable from "../../../components/tables/GithubSimpleTable";
import {
    CircleCancelMinor,
    CircleTickMinor
  } from '@shopify/polaris-icons';

const headers = [
    {
        text: "",
        value: "icon",
        itemOrder: 0,
    },
    {
        text: "Data type",
        value: "subType",
        showFilter:true,
        itemOrder: 1,
    },
    {
        text: "Custom type",
        value: "isCustomType",
        itemOrder: 2,
    },
    {
        text: "API response",
        value: "response",
        itemCell: 2,
    },
    {
        text: "API request",
        value: "request",
        itemCell: 2,
    },
    {
        text:"Sensitive count",
        value: "sensitiveCount"
    }
] 

const sortOptions = [
    { label: 'Sensitive data', value: 'sensitiveCount asc', directionLabel: 'More exposure', sortKey: 'sensitiveCount' },
    { label: 'Sensitive data', value: 'sensitiveCount desc', directionLabel: 'Less exposure', sortKey: 'sensitiveCount' },
    { label: 'Data type', value: 'subType asc', directionLabel: 'A-Z', sortKey: 'subType' },
    { label: 'Data type', value: 'subType desc', directionLabel: 'Z-A', sortKey: 'subType' },
  ];

const resourceName = {
    singular: 'Sensitive data type',
    plural: 'Sensitive data types',
  };

const getActions = (item) => {
    return [{
        items: [{
            content: 'Edit',
            onAction: () => { console.log("edit function for", item) },
        }]
    }]
}

function AllSensitiveData(){

    const [data, setData] = useState([])
    
    useEffect(() => {
        let tmp=[]
        async function fetchData(){
            await api.fetchDataTypes().then((res) => {
                res.dataTypes.aktoDataTypes.forEach((type) => {
                    tmp.push({
                        subType:type.name,
                        request:0,
                        response:0,
                        hexId:type.name,
                        nextUrl:type.name,
                        icon: CircleTickMinor,
                        sensitiveCount:0
                    })
                })
                res.dataTypes.customDataTypes.forEach((type) => {
                    tmp.push({
                        subType:type.name,
                        isCustomType:[{confidence : 'Custom'}],
                        request:0,
                        response:0,
                        hexId:type.name,
                        nextUrl:type.name,
                        icon: type.active ? CircleTickMinor : CircleCancelMinor,
                        sensitiveCount:0
                    })
                })
            })
            await api.fetchSubTypeCountMap(0, func.timeNow()).then((res) => {
                let count = res.response.subTypeCountMap;
                Object.keys(count.REQUEST).map((key) => {
                    tmp.forEach((obj) => {
                        if(obj.subType==key){
                            obj.request=count.REQUEST[key]
                            obj.sensitiveCount=obj.request
                        }
                    })
                })
                Object.keys(count.RESPONSE).map((key) => {
                    tmp.forEach((obj) => {
                        if(obj.subType==key){
                            obj.response=count.RESPONSE[key]
                            obj.sensitiveCount=(obj.response*100000)
                        }
                    })
                })
                setData(tmp);
            })
        }
        fetchData();
    }, [])
    
    return (
        <PageWithMultipleCards
        title={
                <Text variant='headingLg' truncate>
            {
                "Sensitive data exposure"
            }
        </Text>
            }
            primaryAction={<Button primary>Create custom data types</Button>}
            components={[
                <GithubSimpleTable
                key="table"
                data={data} 
                sortOptions={sortOptions} 
                resourceName={resourceName} 
                filters={[]}
                disambiguateLabel={()=>{}} 
                headers={headers}
                hasRowActions={true}
                getActions={getActions}
                
                />
            ]}
        />

    )
}

export default AllSensitiveData