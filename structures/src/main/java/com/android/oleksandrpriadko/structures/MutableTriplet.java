package com.android.oleksandrpriadko.structures;

/**
 * wifi_switcher
 * Created by Oleksandr Priadko.
 * 12/5/17
 */

public class MutableTriplet<F, S, T> {
    public F first;
    public S second;
    public T third;

    /**
     * Constructor for a MutableTriplet.
     *
     * @param first  the first object in the MutableTriplet
     * @param second the second object in the MutableTriplet
     * @param third  the third object in the MutableTriplet
     */
    private MutableTriplet(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Checks the three objects for equality by delegating to their respective
     * {@link Object#equals(Object)} methods.
     *
     * @param o the {@link MutableTriplet} to which this one is to be checked for equality
     * @return true if the underlying objects of the Triple are considered
     * equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MutableTriplet)) {
            return false;
        }
        MutableTriplet<?, ?, ?> p = (MutableTriplet<?, ?, ?>) o;
        return objectsEqual(p.first, first)
                && objectsEqual(p.second, second)
                && objectsEqual(p.third, third);
    }

    private static boolean objectsEqual(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the MutableTriplet
     */
    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode())
                ^ (second == null ? 0 : second.hashCode())
                ^ (third == null ? 0 : third.hashCode());
    }

    @Override
    public String toString() {
        return "MutableTriplet{"
                + String.valueOf(first) + " "
                + String.valueOf(second) + " "
                + String.valueOf(third) + "}";
    }

    /**
     * Convenience method for creating an appropriately typed pair.
     *
     * @param a the first object in the MutableTriplet
     * @param b the second object in the MutableTriplet
     * @param c the third object in the MutableTriplet
     * @return a MutableTriplet that is templatized with the types of a, b and c
     */
    public static <A, B, C> MutableTriplet<A, B, C> create(A a, B b, C c) {
        return new MutableTriplet<>(a, b, c);
    }
}
