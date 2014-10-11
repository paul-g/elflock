package org.paulg.ispend.utils;

import lombok.Data;

@Data
public class Pair<X, Y> {
    public X fst;
    public Y snd;
    public Pair() {}
    public Pair(X fst, Y snd) {
        this.fst = fst;
        this.snd = snd;
    }

}
