/**
 * Source file SearchRecipeControllerTest.java
 */
package au.com.recipe.search.controller;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit Test class for Search Recipe Controller.
 * 
 * @author R Kumar.
 */

public class SearchRecipeControllerTest {

	private static String csvPath;
	private static String jsonPath;

	private SearchRecipeController searchRecipeController;

	@BeforeClass
	public static void onlyOnce() {
		csvPath = getPath("/fridge.csv");
		jsonPath = getPath("/recipes.json");
	}

	@Before
	public void setUp() {
		searchRecipeController = new SearchRecipeController();
	}

	@After
	public void resetSystemTime() {
		DateTimeUtils.setCurrentMillisSystem();
	}

	@Test
	public void getRecipeForToday() throws IOException {
		setFutureDate(2012, 12, 27);
		assertRecipe("salad sandwich");
	}

	@Test
	public void getRecipeForFutureDate() throws IOException {
		setFutureDate(2013, 12, 27);
		assertRecipe("grilled cheese on toast");
	}

	@Test
	public void getRecipeWithExpiredIngredients() throws IOException {
		setFutureDate(2014, 12, 31);
		assertRecipe("Order Takeout");

	}

	private void assertRecipe(final String name) throws IOException {
		assertEquals(name, searchRecipeController.getRecipeForToday(csvPath,
				jsonPath));
	}

	private static String getPath(final String resource) {
		return new File(SearchRecipeControllerTest.class.getResource(resource)
				.getPath()).getAbsolutePath();
	}

	private void setFutureDate(int year, int month, int day) {
		DateTimeUtils.setCurrentMillisFixed(new DateTime().withYear(year)
				.withMonthOfYear(month).withDayOfMonth(day).getMillis());
	}
}
