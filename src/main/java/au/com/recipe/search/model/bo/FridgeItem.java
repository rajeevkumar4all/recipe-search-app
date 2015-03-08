/**
 * Source file FridgeItem.java
 */
package au.com.recipe.search.model.bo;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import org.joda.time.DateTime;
import au.com.recipe.search.common.Unit;

/**
 * Fridge Item business object class.
 * 
 * @author R Kumar.
 */
public class FridgeItem implements Comparable<FridgeItem> {
	/**
	 * The name of the ingredient - e.g. egg.
	 */
	private final String item;
	/**
	 * The amount.
	 */
	private final int amount;
	/**
	 * The unit of the measure values.
	 */
	private final Unit unit;
	/**
	 * The use by date of the ingredient (dd/mm/yy).
	 */
	private final DateTime useBy;

	private FridgeItem(final String item, final int amount, final Unit unit,
			final DateTime useBy) {
		this.item = item;
		this.amount = amount;
		this.unit = unit;
		this.useBy = useBy;
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

	public DateTime getUseBy() {
		return useBy;
	}

	@Override
	public int compareTo(final FridgeItem o) {
		return ComparisonChain.start().compare(useBy, o.getUseBy()).compare(
				item, o.getItem()).result();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("item", item).add("amount",
				amount).add("unit", unit).add("useBy", useBy).toString();
	}

	public static class Builder {

		private String item;
		private int amount;
		private Unit unit;
		private DateTime useBy;

		public FridgeItem build() {
			return new FridgeItem(item, amount, unit, useBy);
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

		public Builder withUseBy(final DateTime useBy) {
			this.useBy = useBy;
			return this;
		}
	}
}
