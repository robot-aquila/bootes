package ru.prolib.bootes.tsgr001a.robot.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ru.prolib.bootes.lib.data.ts.S3TradeSignal;
import ru.prolib.bootes.lib.data.ts.filter.FilterSet;
import ru.prolib.bootes.lib.data.ts.filter.IFilterSet;

public class S3TSFilterSetFactory implements IS3TSFilterSetFactory {
	private final IS3TSFilterFactory filterFactory;
	
	public S3TSFilterSetFactory(IS3TSFilterFactory filterFactory) {
		this.filterFactory = filterFactory;
	}
	
	@Override
	public IFilterSet<S3TradeSignal> produce(String args) {
		FilterSet<S3TradeSignal> filters = new FilterSet<>();
		for ( String code : StringUtils.split(args, ", ") ) {
			filters.addFilter(filterFactory.produce(code));
		}
		return filters;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(891212365, 54411)
				.append(filterFactory)
				.build();
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != S3TSFilterSetFactory.class ) {
			return false;
		}
		S3TSFilterSetFactory o = (S3TSFilterSetFactory) other;
		return new EqualsBuilder()
				.append(o.filterFactory, filterFactory)
				.build();
	}

}
