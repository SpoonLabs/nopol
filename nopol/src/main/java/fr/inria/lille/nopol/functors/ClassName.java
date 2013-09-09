package fr.inria.lille.nopol.functors;

import javax.annotation.Nullable;

import com.google.common.base.Function;

public enum ClassName implements Function<Class<?>, String> {
	INSTANCE;

	@Override
	@Nullable
	public String apply(@Nullable final Class<?> input) {
		return input.getName();
	}
}