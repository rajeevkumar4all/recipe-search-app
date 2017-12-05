/**
 * Source file SearchRecipeController.java
 */
package au.com.recipe.search.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Iterables.tryFind;
import static com.google.common.collect.Sets.newTreeSet;
import au.com.recipe.search.common.SearchRecipeConstants;
import au.com.recipe.search.common.Unit;
import au.com.recipe.search.model.bo.FridgeItem;
import au.com.recipe.search.model.bo.Ingredient;
import au.com.recipe.search.model.bo.Recipe;

/**
 * Search Recipe Controller class.
 * 
 * @author R Kumar.
 */

public final class SearchRecipeController {

	private static final Logger LOGGER = Logger
			.getLogger(SearchRecipeController.class.getName());
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static void main(final String[] args) {
		if (args == null || args.length != 2) {
			LOGGER
					.severe("Must provide fridge CSV and recipes JSON file paths");
			System.exit(0);
		}

		final SearchRecipeController searchRecipe = new SearchRecipeController();
		try {
			LOGGER.info("Tonight's recipe is "
					+ searchRecipe.getRecipeForToday(args[0], args[1]));
		} catch (final IOException e) {
			LOGGER.severe("Failed to get recipe: " + e.getMessage());
		}
	}

	public String getRecipeForToday(final String csvPath, final String jsonPath)
			throws IOException {
		final Set<FridgeItem> usableFridgeItems = getUsableFridgeItems(csvPath);
		final List<Recipe> recipes = readRecipes(jsonPath);
		return getRecipeForToday(usableFridgeItems, recipes);
	}

	public String getRecipeForToday(final Set<FridgeItem> usableFridgeItems,
			final List<Recipe> recipes) throws IOException {
		for (final FridgeItem fridgeItem : usableFridgeItems) {
			final Optional<Recipe> recipeOptional = tryFind(recipes,
					new Predicate<Recipe>() {
						@Override
						public boolean apply(final Recipe recipe) {
							final List<Ingredient> ingredients = recipe
									.getIngredients();
							if (!isIngredientAvailableInFridge(ingredients,
									fridgeItem)) {
								return false;
							}
							final int eligibleFridgeItems = size(filter(
									ingredients, new Predicate<Ingredient>() {
										@Override
										public boolean apply(
												final Ingredient ingredient) {
											return !isEmpty(filter(
													usableFridgeItems,
													new Predicate<FridgeItem>() {
														@Override
														public boolean apply(
																final FridgeItem fridgeItem) {
															return fridgeItem
																	.getItem()
																	.equals(
																			ingredient
																					.getItem())
																	&& fridgeItem
																			.getAmount() >= ingredient
																			.getAmount()
																	&& fridgeItem
																			.getUnit() == ingredient
																			.getUnit();
														}
													}));
										}
									}));
							return eligibleFridgeItems == ingredients.size();
						}
					});
			if (recipeOptional.isPresent()) {
				return recipeOptional.get().getName();
			}
		}
		return SearchRecipeConstants.ORDER_TAKEOUT;
	}

	private Set<FridgeItem> getUsableFridgeItems(final String csvFilePath)
			throws IOException {
		final DateTime today = DateTime.now();
		final List<String> rows = Files.readAllLines(Paths.get(csvFilePath),
				Charset.defaultCharset());
		return newTreeSet(filter(transform(rows,
				new Function<String, FridgeItem>() {
					@Override
					public FridgeItem apply(final String row) {
						final Optional<FridgeItem> fridgeItemOptional = getFridgeItem(row);
						if (fridgeItemOptional.isPresent()) {
							final FridgeItem fridgeItem = fridgeItemOptional
									.get();
							if (Days.daysBetween(today, fridgeItem.getUseBy())
									.getDays() >= 0) {
								return fridgeItem;
							}
						}
						return null;
					}
				}), notNull()));
	}

	private Optional<FridgeItem> getFridgeItem(final String row) {
		if (Strings.isNullOrEmpty(row)) {
			return absent();
		}
		final List<String> fridgeItemElements = Splitter.on(SearchRecipeConstants.DE_LIMITER).splitToList(
				row);
		if (fridgeItemElements.size() != 4) {
			return absent();
		}
		final String item = fridgeItemElements.get(0);
		final int amount = Integer.parseInt(fridgeItemElements.get(1));
		final Unit unit = Unit.valueOf(fridgeItemElements.get(2));
		final DateTime useBy = DateTimeFormat.forPattern(SearchRecipeConstants.DATE_PATTERN)
				.parseDateTime(fridgeItemElements.get(3));
		return of(new FridgeItem.Builder().withItem(item).withAmount(amount)
				.withUnit(unit).withUseBy(useBy).build());
	}

	private List<Recipe> readRecipes(final String recipesFilePath)
			throws IOException {
		final ObjectReader objectReader = MAPPER
				.reader(new TypeReference<List<Recipe>>() {
				});
		return objectReader.readValue(Files.readAllBytes(Paths
				.get(recipesFilePath)));
	}

	private boolean isIngredientAvailableInFridge(
			final List<Ingredient> ingredients, final FridgeItem fridgeItem) {
		return tryFind(ingredients, new Predicate<Ingredient>() {
			@Override
			public boolean apply(final Ingredient ingredient) {
				return ingredient.getItem().equals(fridgeItem.getItem());
			}
		}).isPresent();
	}
}
