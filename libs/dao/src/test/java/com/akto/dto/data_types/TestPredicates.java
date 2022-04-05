package com.akto.dto.data_types;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class TestPredicates {


    @Test
    public void testStartsWithPredicate() {
        StartsWithPredicate startsWithPredicate = new StartsWithPredicate("w13rd");
        Object value = "w13rd_a55";
        boolean result = startsWithPredicate.validate(value);
        assertTrue(result);

        value = "ww13rd_a55";
        result = startsWithPredicate.validate(value);
        assertFalse(result);

        value = "W13rd_a55";
        result = startsWithPredicate.validate(value);
        assertFalse(result);

        value = null;
        result = startsWithPredicate.validate(value);
        assertFalse(result);

        value = 234;
        result = startsWithPredicate.validate(value);
        assertFalse(result);

        value = new HashMap<String,String>();
        result = startsWithPredicate.validate(value);
        assertFalse(result);
    }

    @Test
    public void testEndsWithPredicate() {
        EndsWithPredicate endsWithPredicate = new EndsWithPredicate("a55");
        Object value = "w13rd_a55";
        boolean result = endsWithPredicate.validate(value);
        assertTrue(result);

        value = "w13rd_a553";
        result = endsWithPredicate.validate(value);
        assertFalse(result);

        value = null;
        result = endsWithPredicate.validate(value);
        assertFalse(result);

        value = 234;
        result = endsWithPredicate.validate(value);
        assertFalse(result);

        value = new HashMap<String,String>();
        result = endsWithPredicate.validate(value);
        assertFalse(result);
    }

    @Test
    public void testRegexPredicate() {
        RegexPredicate regexPredicate = new RegexPredicate("^\\d{3}-\\d{2}-\\d{4}$");
        Object value = "234-21-2342";
        boolean result = regexPredicate.validate(value);
        assertTrue(result);

        value = "w13rd_a55";
        result = regexPredicate.validate(value);
        assertFalse(result);

        value = null;
        result = regexPredicate.validate(value);
        assertFalse(result);

        value = 234;
        result = regexPredicate.validate(value);
        assertFalse(result);
        value = new HashMap<String,String>();
        result = regexPredicate.validate(value);
        assertFalse(result);
    }
}
