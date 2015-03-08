/**
 * Source file Ingredient.java 
 */
package au.com.recipe.search.model.bo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;
import au.com.recipe.search.common.Unit;

/**
 * Ingredient business object class.
 * 
 * @author R Kumar.
 */
@JsonDeserialize(builder = Ingredient.Builder.class)
public class Ingredient {
	/**
	 * The name of the ingredient item.
	 */
	private final String item;
	/**
	 * The amount of the ingredient item.
	 */
	private final int amount;
	/**
	 * The quantity of the ingredient item.
	 */
	private final Unit unit;

	private Ingredient(final String item, final int amount, final Unit unit) {
		this.item = item;
		this.amount = amount;
		this.unit = unit;
	}

	public String getItem() {
		return item;
	}

	public int getAmount() {
		return amount;
	}

	public Unit getUnit() {
		return unit;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("item", item).add("amount",
				amount).add("unit", unit).toString();
	}

	public static final class Builder {

		private String item;
		private int amount;
		private Unit unit;

		public Ingredient build() {
			return new Ingredient(item, amount, unit);
		}

		public Builder withItem(final String item) {
			this.item = item;
			return this;
		}

		public Builder withAmount(final int amount) {
			this.amount = amount;
			return this;
		}

		public Builder withUnit(final Unit unit) {
			this.unit = unit;
			return this;
		}
	}
}
