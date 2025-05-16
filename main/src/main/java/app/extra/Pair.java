package app.extra;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Pair<T, R> {
    private final T first;
    private final R second;
}
