package com.android.oleksandrpriadko.structures;

/**
 * wifi_switcher
 * Created by Oleksandr Priadko.
 * 12/5/17
 */

public class MutablePair<F, S>{
    public F first;
    public S second;

    /**
     * Constructor for a MutablePair.
     *
     * @param first the first object in the MutablePair
     * @param second the second object in the pair
     */
    private MutablePair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Checks the two objects for equality by delegating to their respective
     * {@link Object#equals(Object)} methods.
     *
     * @param o the {@link MutablePair} to which this one is to be checked for equality
     * @return true if the underlying objects of the MutablePair are both considered
     *         equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MutablePair)) {
            return false;
        }
        MutablePair<?, ?> p = (MutablePair<?, ?>) o;
        return objectsEqual(p.first, first) && objectsEqual(p.second, second);
    }

    private static boolean objectsEqual(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the MutablePair
     */
    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode()) ^ (second == null ? 0 : second.hashCode());
    }

    @Override
    public String toString() {
        return "MutablePair{" + String.valueOf(first) + " " + String.valueOf(second) + "}";
    }

    /**
     * Convenience method for creating an appropriately typed pair.
     * @param a the first object in the MutablePair
     * @param b the second object in the pair
     * @return a MutablePair that is templatized with the types of a and b
     */
    public static <A, B> MutablePair<A, B> create(A a, B b) {
        return new MutablePair<>(a, b);
    }
}
