package org.jfree.data;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataUtilitiesTest {

    private Mockery context;
    private Values2D values2D;

    @Before
    public void setUp() {
        context = new Mockery();
        values2D = context.mock(Values2D.class);
    }

    // --- calculateColumnTotal(Values2D data, int column) Tests ---

    @Test
    public void calculateColumnTotalWithPositiveValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(10.0));
            oneOf(values2D).getValue(1, 0); will(returnValue(20.0));
            oneOf(values2D).getValue(2, 0); will(returnValue(30.0));
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        assertEquals(60.0, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotalWithNegativeValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 0); will(returnValue(-15.0));
            oneOf(values2D).getValue(1, 0); will(returnValue(-25.0));
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        assertEquals(-40.0, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotalWithNullValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 2); will(returnValue(10.0));
            oneOf(values2D).getValue(1, 2); will(returnValue(null));
            oneOf(values2D).getValue(2, 2); will(returnValue(20.0));
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 2);
        assertEquals(30.0, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotalWithAllNullValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 0); will(returnValue(null));
            oneOf(values2D).getValue(1, 0); will(returnValue(null));
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        assertEquals(0.0, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotalWithEmptyTable() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(0));
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        assertEquals(0.0, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotalWithZeroValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(0.0));
            oneOf(values2D).getValue(1, 0); will(returnValue(0.0));
            oneOf(values2D).getValue(2, 0); will(returnValue(0.0));
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        assertEquals(0.0, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotalWithLargeValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 0); will(returnValue(1000000.5));
            oneOf(values2D).getValue(1, 0); will(returnValue(2000000.5));
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        assertEquals(3000001.0, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotalWithDecimalValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 0); will(returnValue(0.1));
            oneOf(values2D).getValue(1, 0); will(returnValue(0.2));
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        assertEquals(0.3, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotalWithDifferentColumns() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 5); will(returnValue(7.5));
            oneOf(values2D).getValue(1, 5); will(returnValue(2.5));
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 5);
        assertEquals(10.0, result, 0.000000001d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateColumnTotalThrowsExceptionForNullData() {
        
        DataUtilities.calculateColumnTotal(null, 0);
        fail("Expected IllegalArgumentException for null data");
    }

    @Test
    public void calculateColumnTotalWithSingleRow() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(1));
            oneOf(values2D).getValue(0, 0); will(returnValue(42.0));
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        assertEquals(42.0, result, 0.000000001d);
    }

    /* Mutation Edge Cases */
    
    @Test
    public void calculateColumnTotal_ExactIterationCount() {
        //final Values2D data = context.mock(Values2D.class);

        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); 
            will(returnValue(3));

            // 🔥 Enforce EXACT loop behavior
            oneOf(values2D).getValue(0, 0); will(returnValue(1.0));
            oneOf(values2D).getValue(1, 0); will(returnValue(2.0));
            oneOf(values2D).getValue(2, 0); will(returnValue(3.0));
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 0);

        assertEquals(6.0, result, 0.0001);

        context.assertIsSatisfied();
    }
    
    @Test
    public void calculateColumnTotal_LastRowCritical() {

        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); 
            will(returnValue(2));

            oneOf(values2D).getValue(0, 0); will(returnValue(1.0));
            oneOf(values2D).getValue(1, 0); will(returnValue(100.0)); // critical
        }});

        double result = DataUtilities.calculateColumnTotal(values2D, 0);

        assertEquals(101.0, result, 0.0001);

        context.assertIsSatisfied();
    }
    
    // --- calculateRowTotal(Values2D data, int row) Tests ---

    @Test
    public void calculateRowTotalWithPositiveValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(10.0));
            oneOf(values2D).getValue(0, 1); will(returnValue(20.0));
            oneOf(values2D).getValue(0, 2); will(returnValue(30.0));
        }});

        double result = DataUtilities.calculateRowTotal(values2D, 0);
        assertEquals(60.0, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotalWithNegativeValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(2));
            oneOf(values2D).getValue(1, 0); will(returnValue(-15.0));
            oneOf(values2D).getValue(1, 1); will(returnValue(-25.0));
        }});

        double result = DataUtilities.calculateRowTotal(values2D, 1);
        assertEquals(-40.0, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotalWithNullValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(10.0));
            oneOf(values2D).getValue(0, 1); will(returnValue(null));
            oneOf(values2D).getValue(0, 2); will(returnValue(20.0));
        }});

        double result = DataUtilities.calculateRowTotal(values2D, 0);
        assertEquals(30.0, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotalWithAllNullValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(2));
            oneOf(values2D).getValue(1, 0); will(returnValue(null));
            oneOf(values2D).getValue(1, 1); will(returnValue(null));
        }});

        double result = DataUtilities.calculateRowTotal(values2D, 1);
        assertEquals(0.0, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotalWithEmptyRow() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(0));
        }});

        double result = DataUtilities.calculateRowTotal(values2D, 0);
        assertEquals(0.0, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotalWithZeroValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(0.0));
            oneOf(values2D).getValue(0, 1); will(returnValue(0.0));
            oneOf(values2D).getValue(0, 2); will(returnValue(0.0));
        }});

        double result = DataUtilities.calculateRowTotal(values2D, 0);
        assertEquals(0.0, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotalWithLargeValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(2));
            oneOf(values2D).getValue(1, 0); will(returnValue(1000000.5));
            oneOf(values2D).getValue(1, 1); will(returnValue(2000000.5));
        }});

        double result = DataUtilities.calculateRowTotal(values2D, 1);
        assertEquals(3000001.0, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotalWithDecimalValues() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 0); will(returnValue(0.1));
            oneOf(values2D).getValue(0, 1); will(returnValue(0.2));
        }});

        double result = DataUtilities.calculateRowTotal(values2D, 0);
        assertEquals(0.3, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotalWithDifferentRows() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(2));
            oneOf(values2D).getValue(5, 0); will(returnValue(15.0));
            oneOf(values2D).getValue(5, 1); will(returnValue(25.0));
        }});

        double result = DataUtilities.calculateRowTotal(values2D, 5);
        assertEquals(40.0, result, 0.000000001d);
    }
    
    // TODO: Mutation Findings
    @Test(expected = IllegalArgumentException.class)
    public void calculateRowTotalWithDifferentRowsThrowsExceptionForNullData() {
        
    	DataUtilities.calculateRowTotal(null, 0);
        fail("Expected IllegalArgumentException for null data");
    }
    

    // --- getCumulativePercentages(KeyedValues data) Tests ---

    @Test
    public void getCumulativePercentagesWithBasicValues() {
        KeyedValues keyedValues = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            atLeast(1).of(keyedValues).getItemCount(); will(returnValue(3));
            allowing(keyedValues).getKey(0); will(returnValue("A"));
            allowing(keyedValues).getKey(1); will(returnValue("B"));
            allowing(keyedValues).getKey(2); will(returnValue("C"));
            allowing(keyedValues).getValue(0); will(returnValue(10.0));
            allowing(keyedValues).getValue(1); will(returnValue(20.0));
            allowing(keyedValues).getValue(2); will(returnValue(30.0));
        }});

        KeyedValues result = DataUtilities.getCumulativePercentages(keyedValues);

        // Total = 10 + 20 + 30 = 60
        // Cumulative: 10/60, 30/60, 60/60
        assertEquals(0.16666666666666666, result.getValue(0).doubleValue(), 0.0000001d);
        assertEquals(0.5, result.getValue(1).doubleValue(), 0.0000001d);
        assertEquals(1.0, result.getValue(2).doubleValue(), 0.0000001d);
    }

    @Test
    public void getCumulativePercentagesWithZeroValues() {
        KeyedValues keyedValues = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            atLeast(1).of(keyedValues).getItemCount(); will(returnValue(3));
            allowing(keyedValues).getKey(0); will(returnValue("A"));
            allowing(keyedValues).getKey(1); will(returnValue("B"));
            allowing(keyedValues).getKey(2); will(returnValue("C"));
            allowing(keyedValues).getValue(0); will(returnValue(0.0));
            allowing(keyedValues).getValue(1); will(returnValue(0.0));
            allowing(keyedValues).getValue(2); will(returnValue(0.0));
        }});

        KeyedValues result = DataUtilities.getCumulativePercentages(keyedValues);

        // Total = 0 + 0 + 0 = 0
        // Cumulative: 0/0 (undefined), 0/0 (undefined), 0/0 (undefined)
        // However, should handle gracefully without error
        assertTrue(Double.isNaN(result.getValue(0).doubleValue()));
        assertTrue(Double.isNaN(result.getValue(1).doubleValue()));
        assertTrue(Double.isNaN(result.getValue(2).doubleValue()));
    }

    @Test
    public void getCumulativePercentagesWithNullValues() {
        KeyedValues keyedValues = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            atLeast(1).of(keyedValues).getItemCount(); will(returnValue(3));
            allowing(keyedValues).getKey(0); will(returnValue("A"));
            allowing(keyedValues).getKey(1); will(returnValue("B"));
            allowing(keyedValues).getKey(2); will(returnValue("C"));
            allowing(keyedValues).getValue(0); will(returnValue(10.0));
            allowing(keyedValues).getValue(1); will(returnValue(null));
            allowing(keyedValues).getValue(2); will(returnValue(30.0));
        }});

        KeyedValues result = DataUtilities.getCumulativePercentages(keyedValues);

        // Total = 10 + 0 + 30 = 40
        // Cumulative: 10/40, 10/40, 40/40
        assertEquals(0.25, result.getValue(0).doubleValue(), 0.0000001d);
        assertEquals(0.25, result.getValue(1).doubleValue(), 0.0000001d);
        assertEquals(1.0, result.getValue(2).doubleValue(), 0.0000001d);
    }

    @Test
    public void getCumulativePercentagesWithEmptyData() {
        KeyedValues keyedValues = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            atLeast(1).of(keyedValues).getItemCount(); will(returnValue(0));
        }});

        KeyedValues result = DataUtilities.getCumulativePercentages(keyedValues);

        // No items, so cumulative percentages should also be empty
        assertEquals(0, result.getItemCount());
    }

    @Test
    public void getCumulativePercentagesWithSingleItem() {
        KeyedValues keyedValues = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            atLeast(1).of(keyedValues).getItemCount(); will(returnValue(1));
            allowing(keyedValues).getKey(0); will(returnValue("Only"));
            allowing(keyedValues).getValue(0); will(returnValue(50.0));
        }});

        KeyedValues result = DataUtilities.getCumulativePercentages(keyedValues);

        // Total = 50
        // Cumulative: 50/50 = 1.0
        assertEquals(1.0, result.getValue(0).doubleValue(), 0.0000001d);
    }

    @Test
    public void getCumulativePercentagesWithComplexCalculation() {
        KeyedValues keyedValues = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            atLeast(1).of(keyedValues).getItemCount(); will(returnValue(5));
            allowing(keyedValues).getKey(0); will(returnValue("Q1"));
            allowing(keyedValues).getKey(1); will(returnValue("Q2"));
            allowing(keyedValues).getKey(2); will(returnValue("Q3"));
            allowing(keyedValues).getKey(3); will(returnValue("Q4"));
            allowing(keyedValues).getKey(4); will(returnValue("Q5"));
            allowing(keyedValues).getValue(0); will(returnValue(12.5));
            allowing(keyedValues).getValue(1); will(returnValue(37.5));
            allowing(keyedValues).getValue(2); will(returnValue(25.0));
            allowing(keyedValues).getValue(3); will(returnValue(15.0));
            allowing(keyedValues).getValue(4); will(returnValue(10.0));
        }});

        KeyedValues result = DataUtilities.getCumulativePercentages(keyedValues);

        // Total = 12.5 + 37.5 + 25.0 + 15.0 + 10.0 = 100.0
        assertEquals(0.125, result.getValue(0).doubleValue(), 0.0000001d);  // 12.5/100 = 0.125
        assertEquals(0.5, result.getValue(1).doubleValue(), 0.0000001d);    // 50.0/100 = 0.5
        assertEquals(0.75, result.getValue(2).doubleValue(), 0.0000001d);   // 75.0/100 = 0.75
        assertEquals(0.9, result.getValue(3).doubleValue(), 0.0000001d);    // 90.0/100 = 0.9
        assertEquals(1.0, result.getValue(4).doubleValue(), 0.0000001d);    // 100.0/100 = 1.0
    }

    // TODO: Mutation Findings
    @Test(expected = IllegalArgumentException.class)
    public void getCumulativePercentagesThrowsExceptionForNullData() {
        
    	DataUtilities.getCumulativePercentages(null);
        fail("Expected IllegalArgumentException for null data");
    }
    
    @Test
    public void testGetCumulativePercentages_CorrectValues() {
    	final KeyedValues keyedValues = context.mock(KeyedValues.class);
        
        context.checking(new Expectations() {{
            allowing(keyedValues).getItemCount(); will(returnValue(3));

            allowing(keyedValues).getValue(0); will(returnValue(5.0));
            allowing(keyedValues).getValue(1); will(returnValue(9.0));
            allowing(keyedValues).getValue(2); will(returnValue(2.0));

            allowing(keyedValues).getKey(0); will(returnValue("A"));
            allowing(keyedValues).getKey(1); will(returnValue("B"));
            allowing(keyedValues).getKey(2); will(returnValue("C"));
        }});

        KeyedValues result = DataUtilities.getCumulativePercentages(keyedValues);

        // total = 16
        assertEquals(5.0/16.0, result.getValue(0).doubleValue(), 0.0001);
        assertEquals(14.0/16.0, result.getValue(1).doubleValue(), 0.0001);
        assertEquals(16.0/16.0, result.getValue(2).doubleValue(), 0.0001);
        assertEquals(3, result.getItemCount());
    }
    
    // --- createNumberArray(double[] data) Tests ---

    @Test
    public void createNumberArrayWithPositiveValues() {
        double[] input = {1.0, 2.5, 3.14, 4.0};
        Number[] result = DataUtilities.createNumberArray(input);

        assertEquals(4, result.length);
        assertEquals(1.0, result[0].doubleValue(), 0.0000001d);
        assertEquals(2.5, result[1].doubleValue(), 0.0000001d);
        assertEquals(3.14, result[2].doubleValue(), 0.0000001d);
        assertEquals(4.0, result[3].doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberArrayWithNegativeValues() {
        double[] input = {-1.0, -2.5, -3.14, -4.0};
        Number[] result = DataUtilities.createNumberArray(input);

        assertEquals(4, result.length);
        assertEquals(-1.0, result[0].doubleValue(), 0.0000001d);
        assertEquals(-2.5, result[1].doubleValue(), 0.0000001d);
        assertEquals(-3.14, result[2].doubleValue(), 0.0000001d);
        assertEquals(-4.0, result[3].doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberArrayWithMixedValues() {
        double[] input = {-1.5, 0.0, 2.5, -3.14, 100.0};
        Number[] result = DataUtilities.createNumberArray(input);

        assertEquals(5, result.length);
        assertEquals(-1.5, result[0].doubleValue(), 0.0000001d);
        assertEquals(0.0, result[1].doubleValue(), 0.0000001d);
        assertEquals(2.5, result[2].doubleValue(), 0.0000001d);
        assertEquals(-3.14, result[3].doubleValue(), 0.0000001d);
        assertEquals(100.0, result[4].doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberArrayWithZeroValues() {
        double[] input = {0.0, 0.0, 0.0};
        Number[] result = DataUtilities.createNumberArray(input);

        assertEquals(3, result.length);
        assertEquals(0.0, result[0].doubleValue(), 0.0000001d);
        assertEquals(0.0, result[1].doubleValue(), 0.0000001d);
        assertEquals(0.0, result[2].doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberArrayWithSpecialValues() {
        double[] input = {Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
        Number[] result = DataUtilities.createNumberArray(input);

        assertEquals(3, result.length);
        assertTrue(Double.isNaN(result[0].doubleValue()));
        assertEquals(Double.POSITIVE_INFINITY, result[1].doubleValue(), 0.0000001d);
        assertEquals(Double.NEGATIVE_INFINITY, result[2].doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberArrayWithEmptyArray() {
        double[] input = {};
        Number[] result = DataUtilities.createNumberArray(input);

        assertEquals(0, result.length);
        assertNotNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNumberArrayThrowsExceptionForNullData() {
    	DataUtilities.createNumberArray(null);
        fail("Expected IllegalArgumentException for null data");
    }

    @Test
    public void createNumberArrayReturnsCorrectType() {
        double[] input = {1.0, 2.0, 3.0};
        Number[] result = DataUtilities.createNumberArray(input);

        assertNotNull(result);
        assertEquals(3, result.length);

        // Verify each element is a Number (specifically Double)
        for (int i = 0; i < result.length; i++) {
            assertNotNull(result[i]);
            assertTrue(result[i] instanceof Number);
            assertTrue(result[i] instanceof Double);
        }
    }

    @Test
    public void createNumberArrayHandlesPrecisionBoundary() {
        double[] input = {Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_NORMAL};
        Number[] result = DataUtilities.createNumberArray(input);

        assertEquals(3, result.length);
        assertEquals(Double.MAX_VALUE, result[0].doubleValue(), 0.0000001d);
        assertEquals(Double.MIN_VALUE, result[1].doubleValue(), 0.0000001d);
        assertEquals(Double.MIN_NORMAL, result[2].doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberArrayCreatesIndependentCopy() {
        double[] input = {1.0, 2.0, 3.0};
        Number[] result = DataUtilities.createNumberArray(input);

        // Modify original array
        input[0] = 999.0;

        // Result should be unchanged
        assertEquals(1.0, result[0].doubleValue(), 0.0000001d);
        assertEquals(2.0, result[1].doubleValue(), 0.0000001d);
        assertEquals(3.0, result[2].doubleValue(), 0.0000001d);
    }

    // --- createNumberArray2D(double[][] data) Tests ---

    @Test
    public void createNumberArray2DWithBasicValues() {
        double[][] input = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0}
        };
        Number[][] result = DataUtilities.createNumberArray2D(input);

        assertEquals(2, result.length);
        assertEquals(3, result[0].length);
        assertEquals(3, result[1].length);

        // Check first row
        assertEquals(1.0, result[0][0].doubleValue(), 0.0000001d);
        assertEquals(2.0, result[0][1].doubleValue(), 0.0000001d);
        assertEquals(3.0, result[0][2].doubleValue(), 0.0000001d);

        // Check second row
        assertEquals(4.0, result[1][0].doubleValue(), 0.0000001d);
        assertEquals(5.0, result[1][1].doubleValue(), 0.0000001d);
        assertEquals(6.0, result[1][2].doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberArray2DWithMixedValues() {
        double[][] input = {
            {-1.5, 0.0, 2.5},
            {100.0, -50.0, 25.5}
        };
        Number[][] result = DataUtilities.createNumberArray2D(input);

        assertEquals(2, result.length);
        assertEquals(3, result[0].length);
        assertEquals(3, result[1].length);

        // Verify all values
        assertEquals(-1.5, result[0][0].doubleValue(), 0.0000001d);
        assertEquals(0.0, result[0][1].doubleValue(), 0.0000001d);
        assertEquals(2.5, result[0][2].doubleValue(), 0.0000001d);
        assertEquals(100.0, result[1][0].doubleValue(), 0.0000001d);
        assertEquals(-50.0, result[1][1].doubleValue(), 0.0000001d);
        assertEquals(25.5, result[1][2].doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberArray2DWithDifferentRowLengths() {
        double[][] input = {
            {1.0, 2.0},
            {3.0, 4.0, 5.0, 6.0},
            {7.0}
        };
        Number[][] result = DataUtilities.createNumberArray2D(input);

        assertEquals(3, result.length);
        assertEquals(2, result[0].length);
        assertEquals(4, result[1].length);
        assertEquals(1, result[2].length);

        // Verify values
        assertEquals(1.0, result[0][0].doubleValue(), 0.0000001d);
        assertEquals(2.0, result[0][1].doubleValue(), 0.0000001d);
        assertEquals(3.0, result[1][0].doubleValue(), 0.0000001d);
        assertEquals(4.0, result[1][1].doubleValue(), 0.0000001d);
        assertEquals(5.0, result[1][2].doubleValue(), 0.0000001d);
        assertEquals(6.0, result[1][3].doubleValue(), 0.0000001d);
        assertEquals(7.0, result[2][0].doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberArray2DWithEmptyArray() {
        double[][] input = {};
        Number[][] result = DataUtilities.createNumberArray2D(input);

        assertEquals(0, result.length);
        assertNotNull(result);
    }

    @Test
    public void createNumberArray2DWithSpecialValues() {
        double[][] input = {
            {Double.NaN, Double.POSITIVE_INFINITY},
            {Double.NEGATIVE_INFINITY, 0.0}
        };
        Number[][] result = DataUtilities.createNumberArray2D(input);

        assertEquals(2, result.length);
        assertEquals(2, result[0].length);
        assertEquals(2, result[1].length);

        assertTrue(Double.isNaN(result[0][0].doubleValue()));
        assertEquals(Double.POSITIVE_INFINITY, result[0][1].doubleValue(), 0.0000001d);
        assertEquals(Double.NEGATIVE_INFINITY, result[1][0].doubleValue(), 0.0000001d);
        assertEquals(0.0, result[1][1].doubleValue(), 0.0000001d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNumberArray2DThrowsExceptionForNullData() {
        
        DataUtilities.createNumberArray2D(null);
        fail("Expected IllegalArgumentException for null data");
    }

    @Test
    public void createNumberArray2DReturnsCorrectType() {
        double[][] input = {{1.0, 2.0}, {3.0, 4.0}};
        Number[][] result = DataUtilities.createNumberArray2D(input);

        assertNotNull(result);
        assertEquals(2, result.length);

        // Verify each row is a Number array
        for (int i = 0; i < result.length; i++) {
            assertNotNull(result[i]);
            assertTrue(result[i] instanceof Number[]);
            for (int j = 0; j < result[i].length; j++) {
                assertNotNull(result[i][j]);
                assertTrue(result[i][j] instanceof Number);
                assertTrue(result[i][j] instanceof Double);
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNumberArray2DWithNullInnerArrays() {
        double[][] input = {null, {1.0, 2.0}, null};
        
        DataUtilities.createNumberArray2D(input);
        fail("Expected IllegalArgumentException for null inner array");        
    }

    @Test
    public void createNumberArray2DCreatesIndependentCopy() {
        double[][] input = {{1.0, 2.0}, {3.0, 4.0}};
        Number[][] result = DataUtilities.createNumberArray2D(input);

        // Modify original array
        input[0][0] = 999.0;
        input[1] = new double[]{777.0, 888.0};

        // Result should be unchanged
        assertEquals(1.0, result[0][0].doubleValue(), 0.0000001d);
        assertEquals(2.0, result[0][1].doubleValue(), 0.0000001d);
        assertEquals(3.0, result[1][0].doubleValue(), 0.0000001d);
        assertEquals(4.0, result[1][1].doubleValue(), 0.0000001d);
    }

    @Test
    public void createNumberArray2DWithExtremeBoundaryValues() {
        double[][] input = {
            {Double.MAX_VALUE, Double.MIN_VALUE},
            {Double.MIN_NORMAL, -Double.MAX_VALUE}
        };
        Number[][] result = DataUtilities.createNumberArray2D(input);

        assertEquals(2, result.length);
        assertEquals(2, result[0].length);
        assertEquals(2, result[1].length);

        assertEquals(Double.MAX_VALUE, result[0][0].doubleValue(), 0.0000001d);
        assertEquals(Double.MIN_VALUE, result[0][1].doubleValue(), 0.0000001d);
        assertEquals(Double.MIN_NORMAL, result[1][0].doubleValue(), 0.0000001d);
        assertEquals(-Double.MAX_VALUE, result[1][1].doubleValue(), 0.0000001d);
    }

    // --- calculateColumnTotal(Values2D data, int column, int[] validRows) Tests ---

    @Test
    public void calculateColumnTotal_WithValidRows_ShouldSumOnlyValidRows() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(5.0));
            oneOf(values2D).getValue(2, 0); will(returnValue(2.0));
        }});
        int[] validRows = {0, 2};
        double result = DataUtilities.calculateColumnTotal(values2D, 0, validRows);
        assertEquals(7.0, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotal_WithEmptyValidRows_ShouldReturnZero() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(2));
        }});
        int[] validRows = {};
        double result = DataUtilities.calculateColumnTotal(values2D, 0, validRows);
        assertEquals(0.0, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotal_WithRowOutOfRange_ShouldIgnoreIt() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 0); will(returnValue(4.0));
        }});
        int[] validRows = {0, 5};
        double result = DataUtilities.calculateColumnTotal(values2D, 0, validRows);
        assertEquals(4.0, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotal_WithNullValueInValidRows_ShouldSkipNull() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 0); will(returnValue(null));
            oneOf(values2D).getValue(1, 0); will(returnValue(3.0));
        }});
        int[] validRows = {0, 1};
        double result = DataUtilities.calculateColumnTotal(values2D, 0, validRows);
        assertEquals(3.0, result, 0.000000001d);
    }

    @Test
    public void calculateColumnTotal_WithAllValidRows_ShouldSumAll() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(1.0));
            oneOf(values2D).getValue(1, 0); will(returnValue(2.0));
            oneOf(values2D).getValue(2, 0); will(returnValue(3.0));
        }});
        int[] validRows = {0, 1, 2};
        double result = DataUtilities.calculateColumnTotal(values2D, 0, validRows);
        assertEquals(6.0, result, 0.000000001d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateColumnTotal_WithValidRowsNullData_ShouldThrowException() {
        
        DataUtilities.calculateColumnTotal(null, 0, new int[]{0});
        fail("Expected IllegalArgumentException for null data");
    }

    // --- calculateRowTotal(Values2D data, int row, int[] validCols) Tests ---

    @Test
    public void calculateRowTotal_WithValidCols_ShouldSumOnlyValidCols() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(5.0));
            oneOf(values2D).getValue(0, 2); will(returnValue(2.0));
        }});
        int[] validCols = {0, 2};
        double result = DataUtilities.calculateRowTotal(values2D, 0, validCols);
        assertEquals(7.0, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotal_WithEmptyValidCols_ShouldReturnZero() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(2));
        }});
        int[] validCols = {};
        double result = DataUtilities.calculateRowTotal(values2D, 0, validCols);
        assertEquals(0.0, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotal_WithColOutOfRange_ShouldIgnoreIt() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 0); will(returnValue(4.0));
        }});
        int[] validCols = {0, 5};
        double result = DataUtilities.calculateRowTotal(values2D, 0, validCols);
        assertEquals(4.0, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotal_WithNullValueInValidCols_ShouldSkipNull() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 0); will(returnValue(null));
            oneOf(values2D).getValue(0, 1); will(returnValue(3.0));
        }});
        int[] validCols = {0, 1};
        double result = DataUtilities.calculateRowTotal(values2D, 0, validCols);
        assertEquals(3.0, result, 0.000000001d);
    }

    @Test
    public void calculateRowTotal_WithAllValidCols_ShouldSumAll() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(1.0));
            oneOf(values2D).getValue(0, 1); will(returnValue(2.0));
            oneOf(values2D).getValue(0, 2); will(returnValue(3.0));
        }});
        int[] validCols = {0, 1, 2};
        double result = DataUtilities.calculateRowTotal(values2D, 0, validCols);
        assertEquals(6.0, result, 0.000000001d);
    }

    @Test(expected = IllegalArgumentException.class)
    public void calculateRowTotal_WithValidColsNullData_ShouldThrowException() {

        DataUtilities.calculateRowTotal(null, 0, new int[]{0});
        fail("Expected IllegalArgumentException for null data");        
    }

    // --- equal(double[][] a, double[][] b) Tests ---

    @Test
    public void equal_TwoEqualArrays_ShouldReturnTrue() {
        double[][] a = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] b = {{1.0, 2.0}, {3.0, 4.0}};
        assertTrue(DataUtilities.equal(a, b));
    }

    @Test
    public void equal_TwoDifferentArrays_ShouldReturnFalse() {
        double[][] a = {{1.0, 2.0}};
        double[][] b = {{3.0, 4.0}};
        assertFalse(DataUtilities.equal(a, b));
    }

    @Test
    public void equal_BothNull_ShouldReturnTrue() {
        assertTrue(DataUtilities.equal(null, null));
    }

    @Test
    public void equal_FirstNull_ShouldReturnFalse() {
        double[][] b = {{1.0, 2.0}};
        assertFalse(DataUtilities.equal(null, b));
    }

    @Test
    public void equal_SecondNull_ShouldReturnFalse() {
        double[][] a = {{1.0, 2.0}};
        assertFalse(DataUtilities.equal(a, null));
    }

    @Test
    public void equal_DifferentLengths_ShouldReturnFalse() {
        double[][] a = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] b = {{1.0, 2.0}};
        assertFalse(DataUtilities.equal(a, b));
    }
    
    @Test
    public void testMultiRowDifference() {
        double[][] array1 = {
            {1.0, 2.0}, 
            {3.0, 4.0}  // The difference is here
        };
        double[][] array2 = {
            {1.0, 2.0}, 
            {9.9, 9.9}  // The first rows match, but the second doesn't
        };

        // If the mutation changes the loop so it only checks the first row,
        // this will incorrectly return 'true', and the test will fail (killing the mutant).
        assertFalse(DataUtilities.equal(array1, array2));
    }
    
//    @Test
//    public void testEqual_EmptyArrays() {
//        double[][] a = new double[0][];
//        double[][] b = new double[0][];
//        assertTrue(DataUtilities.equal(a, b));
//    }
//    
//    @Test
//    public void testEqual_BothNull() {
//        assertTrue(DataUtilities.equal(null, null));
//    }
//
//    @Test
//    public void testEqual_FirstNull_SecondNotNull() {
//        double[][] b = { {1.0, 2.0} };
//        assertFalse(DataUtilities.equal(null, b));
//    }
    

    // --- clone(double[][] source) Tests ---

    @Test
    public void clone_ValidArray_ShouldReturnEqualArray() {
        double[][] source = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] result = DataUtilities.clone(source);
        assertTrue(DataUtilities.equal(source, result));
    }

    @Test
    public void clone_ValidArray_ShouldReturnDifferentObject() {
        double[][] source = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] result = DataUtilities.clone(source);
        assertNotSame(source, result);
    }

    @Test
    public void clone_ArrayWithNullRow_ShouldHandleNull() {
        double[][] source = new double[2][];
        source[0] = new double[]{1.0, 2.0};
        source[1] = null;
        double[][] result = DataUtilities.clone(source);
        assertNull(result[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void clone_NullData_ShouldThrowException() {
        
        DataUtilities.clone(null);
        fail("Expected IllegalArgumentException for null data");
    }

    @Test
    public void clone_EmptyArray_ShouldReturnEmptyArray() {
        double[][] source = {};
        double[][] result = DataUtilities.clone(source);
        assertEquals(0, result.length);
    }
    
    /* Additional Edge Cases */
    /**
     * KILLS: "changed conditional boundary r < rowCount → r <= rowCount"
     * With rowCount=3, r<=rowCount would try getValue(3,0) which isn't mocked —
     * JMock will throw an unexpected-invocation error, failing the test.
     */
    @Test
    public void calculateColumnTotal_BoundaryMutation_LoopDoesNotExceedRowCount() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(1.0));
            oneOf(values2D).getValue(1, 0); will(returnValue(2.0));
            oneOf(values2D).getValue(2, 0); will(returnValue(3.0));
            // getValue(3, 0) must NOT be called — JMock enforces this via oneOf
        }});
        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        assertEquals(6.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    /**
     * KILLS: "removed conditional — if (n != null) always true"
     * A null in the middle with known non-null values means skipping null
     * changes the sum. If null-guard is removed, NullPointerException is thrown.
     */
    @Test
    public void calculateColumnTotal_NullGuardRemovalMutation_NullSkipped() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(5.0));
            oneOf(values2D).getValue(1, 0); will(returnValue(null));
            oneOf(values2D).getValue(2, 0); will(returnValue(10.0));
        }});
        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        // If null guard removed → NPE. If null treated as 0 → still 15. Exact value kills both.
        assertEquals(15.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    /**
     * KILLS: "replaced += with -= in total accumulation"
     * Negative values with subtraction would produce wrong positive result.
     * -5 + -10 = -15; with subtraction -(-5) + -(-10) = +15
     */
    @Test
    public void calculateColumnTotal_SubstitutionMutation_AdditionNotSubtraction() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 0); will(returnValue(-5.0));
            oneOf(values2D).getValue(1, 0); will(returnValue(-10.0));
        }});
        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        assertEquals(-15.0, result, 1e-9);
    }

    /**
     * KILLS: "return 0 instead of total" (void return / constant return mutant)
     * Single row ensures result must equal exactly that one non-trivial value.
     */
    @Test
    public void calculateColumnTotal_ReturnZeroMutation_ReturnsActualSum() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(1));
            oneOf(values2D).getValue(0, 0); will(returnValue(99.0));
        }});
        double result = DataUtilities.calculateColumnTotal(values2D, 0);
        assertNotEquals(0.0, result, 1e-9);
        assertEquals(99.0, result, 1e-9);
    }

    // =========================================================================
    // calculateRowTotal(Values2D, int)
    // =========================================================================

    /**
     * KILLS: "changed conditional boundary c < columnCount → c <= columnCount"
     * JMock strict oneOf enforces getValue(3, x) is never called.
     */
    @Test
    public void calculateRowTotal_BoundaryMutation_LoopDoesNotExceedColumnCount() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(1.0));
            oneOf(values2D).getValue(0, 1); will(returnValue(2.0));
            oneOf(values2D).getValue(0, 2); will(returnValue(3.0));
            // getValue(0, 3) must NOT be called
        }});
        double result = DataUtilities.calculateRowTotal(values2D, 0);
        assertEquals(6.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    /**
     * KILLS: "replaced += with -= in row total"
     */
    @Test
    public void calculateRowTotal_SubstitutionMutation_AdditionNotSubtraction() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(2));
            oneOf(values2D).getValue(0, 0); will(returnValue(-3.0));
            oneOf(values2D).getValue(0, 1); will(returnValue(-7.0));
        }});
        double result = DataUtilities.calculateRowTotal(values2D, 0);
        assertEquals(-10.0, result, 1e-9);
    }

    /**
     * KILLS: "null guard removed — if (n != null) always true"
     */
    @Test
    public void calculateRowTotal_NullGuardRemovalMutation_NullSkipped() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(4.0));
            oneOf(values2D).getValue(0, 1); will(returnValue(null));
            oneOf(values2D).getValue(0, 2); will(returnValue(6.0));
        }});
        double result = DataUtilities.calculateRowTotal(values2D, 0);
        assertEquals(10.0, result, 1e-9);
    }

    // =========================================================================
    // calculateColumnTotal(Values2D, int, int[] validRows)
    // =========================================================================

    /**
     * KILLS: "changed conditional boundary validRow < rowCount → validRow <= rowCount"
     * Row index = rowCount-1 is valid; row index = rowCount is not.
     * Using boundary index rowCount exactly tests the off-by-one.
     */
    @Test
    public void calculateColumnTotal_ValidRows_BoundaryMutation_ExactRowCountExcluded() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(3));
            oneOf(values2D).getValue(2, 0); will(returnValue(7.0));
            // getValue(3, 0) must NOT be called if mutant changes < to <=
        }});
        // validRows includes exactly rowCount (3) which is out of range
        int[] validRows = {2, 3};
        double result = DataUtilities.calculateColumnTotal(values2D, 0, validRows);
        assertEquals(7.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    /**
     * KILLS: "replaced += with -= in valid-rows column total"
     */
    @Test
    public void calculateColumnTotal_ValidRows_SubstitutionMutation_CorrectSum() {
        context.checking(new Expectations() {{
            oneOf(values2D).getRowCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(-4.0));
            oneOf(values2D).getValue(2, 0); will(returnValue(-6.0));
        }});
        int[] validRows = {0, 2};
        double result = DataUtilities.calculateColumnTotal(values2D, 0, validRows);
        assertEquals(-10.0, result, 1e-9);
    }

    // =========================================================================
    // calculateRowTotal(Values2D, int, int[] validCols)
    // =========================================================================

    /**
     * KILLS: "changed conditional boundary col < colCount → col <= colCount"
     * validCols includes exactly colCount which should be excluded.
     */
    @Test
    public void calculateRowTotal_ValidCols_BoundaryMutation_ExactColCountExcluded() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 2); will(returnValue(8.0));
            // getValue(0, 3) must NOT be called
        }});
        int[] validCols = {2, 3};
        double result = DataUtilities.calculateRowTotal(values2D, 0, validCols);
        assertEquals(8.0, result, 1e-9);
        context.assertIsSatisfied();
    }

    /**
     * KILLS: "replaced += with -= in valid-cols row total"
     */
    @Test
    public void calculateRowTotal_ValidCols_SubstitutionMutation_CorrectSum() {
        context.checking(new Expectations() {{
            oneOf(values2D).getColumnCount(); will(returnValue(3));
            oneOf(values2D).getValue(0, 0); will(returnValue(-2.0));
            oneOf(values2D).getValue(0, 1); will(returnValue(-8.0));
        }});
        int[] validCols = {0, 1};
        double result = DataUtilities.calculateRowTotal(values2D, 0, validCols);
        assertEquals(-10.0, result, 1e-9);
    }

    // =========================================================================
    // getCumulativePercentages(KeyedValues)
    // =========================================================================

    /**
     * KILLS: "less than to not equal → i < getItemCount() becomes i != getItemCount()"
     * AND "replaced += with -= in running total"
     * AND "replaced += with -= in total accumulation"
     * Uses 2 items with distinct, asymmetric values — all intermediate values
     * must be precisely correct, not just the last one.
     */
    @Test
    public void getCumulativePercentages_IntermediateValues_MustBeMonotonicallyIncreasing() {
        KeyedValues kv = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            allowing(kv).getItemCount(); will(returnValue(2));
            allowing(kv).getKey(0);      will(returnValue("X"));
            allowing(kv).getKey(1);      will(returnValue("Y"));
            allowing(kv).getValue(0);    will(returnValue(1.0));
            allowing(kv).getValue(1);    will(returnValue(3.0));
        }});
        KeyedValues result = DataUtilities.getCumulativePercentages(kv);
        // total = 4.0; X = 1/4 = 0.25; Y = 4/4 = 1.0
        assertEquals(2, result.getItemCount());                              // kills loop count mutants
        assertEquals(0.25, result.getValue(0).doubleValue(), 1e-9);         // kills total/runningTotal mutants
        assertEquals(1.0,  result.getValue(1).doubleValue(), 1e-9);
        assertTrue(result.getValue(0).doubleValue() < result.getValue(1).doubleValue()); // kills subtraction mutant
    }

    /**
     * KILLS: "removed conditional — if (v != null) always true" in TOTAL loop
     * If null-guard is removed on the total accumulation loop, total is wrong,
     * making all percentages wrong.
     */
    @Test
    public void getCumulativePercentages_NullGuardInTotalLoop_NullExcludedFromTotal() {
        KeyedValues kv = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            allowing(kv).getItemCount(); will(returnValue(3));
            allowing(kv).getKey(0);      will(returnValue("A"));
            allowing(kv).getKey(1);      will(returnValue("B"));
            allowing(kv).getKey(2);      will(returnValue("C"));
            allowing(kv).getValue(0);    will(returnValue(2.0));
            allowing(kv).getValue(1);    will(returnValue(null)); // null in total loop
            allowing(kv).getValue(2);    will(returnValue(2.0));
        }});
        KeyedValues result = DataUtilities.getCumulativePercentages(kv);
        // total = 2 + 0 + 2 = 4 (null skipped)
        // cumulative: 2/4=0.5, 2/4=0.5, 4/4=1.0
        assertEquals(0.5,  result.getValue(0).doubleValue(), 1e-9);
        assertEquals(0.5,  result.getValue(1).doubleValue(), 1e-9); // null item: running total unchanged
        assertEquals(1.0,  result.getValue(2).doubleValue(), 1e-9);
    }

    /**
     * KILLS: "removed conditional — if (v != null) always true" in RUNNING TOTAL loop
     * Null in running total should not advance runningTotal.
     */
    @Test
    public void getCumulativePercentages_NullGuardInRunningTotalLoop_RunningTotalNotAdvanced() {
        KeyedValues kv = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            allowing(kv).getItemCount(); will(returnValue(3));
            allowing(kv).getKey(0);      will(returnValue("A"));
            allowing(kv).getKey(1);      will(returnValue("B"));
            allowing(kv).getKey(2);      will(returnValue("C"));
            allowing(kv).getValue(0);    will(returnValue(3.0));
            allowing(kv).getValue(1);    will(returnValue(null));
            allowing(kv).getValue(2);    will(returnValue(7.0));
        }});
        KeyedValues result = DataUtilities.getCumulativePercentages(kv);
        // total = 10.0; running: 3/10=0.3, 3/10=0.3 (null skipped), 10/10=1.0
        assertEquals(0.3, result.getValue(0).doubleValue(), 1e-9);
        assertEquals(0.3, result.getValue(1).doubleValue(), 1e-9); // MUST equal previous, not advance
        assertEquals(1.0, result.getValue(2).doubleValue(), 1e-9);
    }

    /**
     * KILLS: "return value substitution — return empty result instead of populated"
     * Checks both item count AND key mapping to ensure result is fully populated.
     */
    @Test
    public void getCumulativePercentages_ResultKeysMustMatchInputKeys() {
        KeyedValues kv = context.mock(KeyedValues.class);
        context.checking(new Expectations() {{
            allowing(kv).getItemCount(); will(returnValue(2));
            allowing(kv).getKey(0);      will(returnValue("first"));
            allowing(kv).getKey(1);      will(returnValue("second"));
            allowing(kv).getValue(0);    will(returnValue(1.0));
            allowing(kv).getValue(1);    will(returnValue(1.0));
        }});
        KeyedValues result = DataUtilities.getCumulativePercentages(kv);
        assertEquals(2,    result.getItemCount());
        assertEquals(0.5,  result.getValue("first").doubleValue(),  1e-9);
        assertEquals(1.0,  result.getValue("second").doubleValue(), 1e-9);
    }

    // =========================================================================
    // equal(double[][], double[][])
    // =========================================================================

    /**
     * KILLS: "negated conditional — return true instead of false when b is null"
     */
    @Test
    public void equal_ANullBNotNull_MustReturnFalse() {
        double[][] b = {{1.0}};
        boolean result = DataUtilities.equal(null, b);
        assertFalse(result); // if negated: returns true → test fails → mutant killed
    }

    /**
     * KILLS: "negated conditional — return false instead of true when both null"
     */
    @Test
    public void equal_BothNull_MustReturnTrue() {
        assertTrue(DataUtilities.equal(null, null));
    }

    /**
     * KILLS: "negated return on length mismatch — returns true when lengths differ"
     */
    @Test
    public void equal_DifferentOuterLength_MustReturnFalse() {
        double[][] a = {{1.0}, {2.0}};
        double[][] b = {{1.0}};
        assertFalse(DataUtilities.equal(a, b));
    }

    /**
     * KILLS: "negated return — Arrays.equals result inverted"
     * First rows match, second rows differ — ensures the inner loop runs
     * past iteration 0 and the mismatch at index 1 is caught.
     */
    @Test
    public void equal_FirstRowMatchSecondRowDiffers_MustReturnFalse() {
        double[][] a = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] b = {{1.0, 2.0}, {5.0, 6.0}};
        assertFalse(DataUtilities.equal(a, b));
    }

    /**
     * KILLS: "loop body never executes — i++ replaced with i-- or i initialized wrong"
     * All 3 rows must be checked; if loop is broken, last-row difference is missed.
     */
    @Test
    public void equal_ThreeRowsLastDiffers_MustReturnFalse() {
        double[][] a = {{1.0}, {2.0}, {3.0}};
        double[][] b = {{1.0}, {2.0}, {9.0}};
        assertFalse(DataUtilities.equal(a, b));
    }

    /**
     * KILLS: "return false → return true at end of method"
     * Verifies that truly equal arrays return true (guards against flipped final return).
     */
    @Test
    public void equal_IdenticalArrays_MustReturnTrue() {
        double[][] a = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] b = {{1.0, 2.0}, {3.0, 4.0}};
        assertTrue(DataUtilities.equal(a, b));
    }

    // =========================================================================
    // clone(double[][])
    // =========================================================================

    /**
     * KILLS: "removed System.arraycopy — row is allocated but never filled"
     * If arraycopy is removed, all values in cloned rows stay 0.0.
     */
    @Test
    public void clone_ArrayCopyMutation_ValuesMustBePreserved() {
        double[][] source = {{10.0, 20.0}, {30.0, 40.0}};
        double[][] result = DataUtilities.clone(source);
        assertEquals(10.0, result[0][0], 1e-9);
        assertEquals(20.0, result[0][1], 1e-9);
        assertEquals(30.0, result[1][0], 1e-9);
        assertEquals(40.0, result[1][1], 1e-9);
    }

    /**
     * KILLS: "null check negated — if (rowSource != null) becomes if (rowSource == null)"
     * With a null row present: if check is inverted, non-null rows get skipped
     * and null rows get copied (causing NPE or wrong result).
     */
    @Test
    public void clone_NullRowPresent_NonNullRowsMustStillBeCopied() {
        double[][] source = new double[3][];
        source[0] = new double[]{1.0, 2.0};
        source[1] = null;
        source[2] = new double[]{5.0, 6.0};
        double[][] result = DataUtilities.clone(source);
        assertNotNull(result[0]);
        assertNull(result[1]);
        assertNotNull(result[2]);
        assertEquals(1.0, result[0][0], 1e-9);
        assertEquals(5.0, result[2][0], 1e-9);
    }

    /**
     * KILLS: "return source instead of clone (reference return mutant)"
     * Deep-independence check: mutating source after clone must not affect result.
     */
    @Test
    public void clone_ModifySource_MustNotAffectClone() {
        double[][] source = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] result = DataUtilities.clone(source);
        source[0][0] = 999.0;
        assertEquals(1.0, result[0][0], 1e-9); // if same reference returned → 999.0 → test fails
    }

    /**
     * KILLS: "clone array size wrong — new double[source.length-1][]"
     */
    @Test
    public void clone_ResultLengthMustMatchSource() {
        double[][] source = {{1.0}, {2.0}, {3.0}};
        double[][] result = DataUtilities.clone(source);
        assertEquals(source.length, result.length);
    }

    // =========================================================================
    // createNumberArray(double[])
    // =========================================================================

    /**
     * KILLS: "result[i] = null instead of new Double(data[i])"
     * Every element must be non-null and hold the correct value.
     */
    @Test
    public void createNumberArray_AllElementsMustBeNonNullAndCorrect() {
        double[] input = {7.0, -3.0, 0.0};
        Number[] result = DataUtilities.createNumberArray(input);
        for (int i = 0; i < input.length; i++) {
            assertNotNull("Element " + i + " must not be null", result[i]);
            assertEquals(input[i], result[i].doubleValue(), 1e-9);
        }
    }

    /**
     * KILLS: "return new Number[0] instead of new Number[data.length]"
     */
    @Test
    public void createNumberArray_LengthMustMatchInput() {
        double[] input = {1.0, 2.0, 3.0, 4.0};
        Number[] result = DataUtilities.createNumberArray(input);
        assertEquals(input.length, result.length);
    }

    /**
     * KILLS: "loop boundary i < data.length → i <= data.length"
     * JMock-style: single element means i=0 is valid, i=1 is not.
     * Verifies result length so any out-of-bounds write would cause ArrayIndexOutOfBounds.
     */
    @Test
    public void createNumberArray_SingleElement_ExactIteration() {
        double[] input = {42.0};
        Number[] result = DataUtilities.createNumberArray(input);
        assertEquals(1, result.length);
        assertEquals(42.0, result[0].doubleValue(), 1e-9);
    }

    // =========================================================================
    // createNumberArray2D(double[][])
    // =========================================================================

    /**
     * KILLS: "outer loop boundary i < l1 → i <= l1"
     */
    @Test
    public void createNumberArray2D_OuterLoopBoundary_ExactRowCount() {
        double[][] input = {{1.0, 2.0}, {3.0, 4.0}};
        Number[][] result = DataUtilities.createNumberArray2D(input);
        assertEquals(2, result.length); // ArrayIndexOutOfBounds if mutant runs i<=l1
        assertEquals(1.0, result[0][0].doubleValue(), 1e-9);
        assertEquals(3.0, result[1][0].doubleValue(), 1e-9);
    }

    /**
     * KILLS: "result[i] = null instead of createNumberArray(data[i])"
     */
    @Test
    public void createNumberArray2D_AllRowsMustBeNonNull() {
        double[][] input = {{1.0}, {2.0}, {3.0}};
        Number[][] result = DataUtilities.createNumberArray2D(input);
        for (int i = 0; i < result.length; i++) {
            assertNotNull("Row " + i + " must not be null", result[i]);
        }
    }

    /**
     * KILLS: "return empty 2D array instead of populated one"
     * Checks length of inner arrays too, not just outer.
     */
    @Test
    public void createNumberArray2D_InnerArrayLengthsMustMatchInput() {
        double[][] input = {{1.0, 2.0, 3.0}, {4.0, 5.0}};
        Number[][] result = DataUtilities.createNumberArray2D(input);
        assertEquals(3, result[0].length);
        assertEquals(2, result[1].length);
    }

}
