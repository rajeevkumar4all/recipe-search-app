/**
 * Source file Recipe.java
 */
package au.com.recipe.search.model.bo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Recipe business object class.
 * 
 * @author R Kumar.
 */
@JsonDeserialize(builder = Recipe.Builder.class)
public class Recipe {
	/**
	 * The name of the Recipe.
	 */
	private final String name;
	/**
	 * The list of the Recipe's ingredients.
	 */
	private final List<Ingredient> ingredients = newArrayList();

	private Recipe(final String name, final List<Ingredient> ingredients) {
		this.name = name;
		if (ingredients != null && !ingredients.isEmpty()) {
			this.ingredients.addAll(ingredients);
		}
	}

	public String getName() {
		return name;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("name", name).add(
				"ingredients", ingredients).toString();
	}

	public static class Builder {

		private String name;
		private List<Ingredient> ingredients = newArrayList();

		public Recipe build() {
			return new Recipe(name, ingredients);
		}

		public Builder withName(final String name) {
			this.name = name;
			return this;
		}

		public Builder withIngredients(final List<Ingredient> ingredients) {
			if (ingredients != null) {
				this.ingredients.addAll(ingredients);
			}
			return this;
		}

		public Builder withIngredient(final Ingredient ingredient) {
			if (ingredient != null) {
				ingredients.add(ingredient);
			}
			return this;
		}
	}
}
