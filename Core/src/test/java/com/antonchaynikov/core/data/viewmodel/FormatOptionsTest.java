package com.antonchaynikov.core.data.viewmodel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FormatOptionsTest {

    @Test
    public void getAnyFormat_shouldReturnDefault_ifNotSet() {
        FormatOptions options = new FormatOptions();

        assertEquals(FormatOptions.DATE_FORMAT_DEFAULT, options.getStartDatePattern());
        assertEquals(FormatOptions.DATE_FORMAT_DEFAULT, options.getEndDatePattern());
        assertEquals(FormatOptions.UNIT_SPEED_DEFAULT, options.getUnitSpeed());
        assertEquals(FormatOptions.UNIT_DISTANCE_DEFAULT, options.getUnitDistance());
    }

    @Test
    public void getEndDate_shouldReturnStartDateFormat_ifStartDateFormatSet_ifEndDateFormatNotSet() {
        FormatOptions options = new FormatOptions();
        String pattern = "MM.dd HH.yy";
        options.setStartDatePattern(pattern);
        assertEquals(pattern, options.getEndDatePattern());
    }
}