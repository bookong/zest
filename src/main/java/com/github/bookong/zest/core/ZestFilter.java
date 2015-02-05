package com.github.bookong.zest.core;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;

public class ZestFilter extends Filter {
	private Filter filter;
	
	public ZestFilter(Filter filter) {
		this.filter = filter;
	}

	@Override
	public boolean shouldRun(Description description) {
		String desiredDescription = filter.describe();
		if (description.isTest()) {
			String desc = description.toString();
			int start = desc.indexOf("[");
			int end = desc.indexOf("]");
			if (start > 0 && end > 0) {
				desc = desc.substring(0, start - 1) + desc.substring(end + 2);
			}
			
			desc = "Method " + desc;

			return desiredDescription.equals(desc);
        }

        // explicitly check if any children want to run
        for (Description each : description.getChildren()) {
            if (shouldRun(each)) {
                return true;
            }
        }
        return false;
	}

	@Override
	public String describe() {
		return filter.describe();
	}
}
