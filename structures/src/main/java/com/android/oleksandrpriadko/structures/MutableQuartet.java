package com.android.oleksandrpriadko.structures;

public class MutableQuartet<F, S, T, Ft> {
    public F first;
    public S second;
    public T third;
    public Ft fourth;

    /**
     * Constructor for a MutableQuartet.
     *
     * @param first  the first object in the MutableQuartet
     * @param second the second object in the MutableQuartet
     * @param third  the third object in the MutableQuartet
     * @param fourth the fourth object in the MutableQuartet
     */
    private MutableQuartet(F first, S second, T third, Ft fourth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
    }

    /**
     * Checks the three objects for equality by delegating to their respective
     * {@link Object#equals(Object)} methods.
     *
     * @param o the {@link MutableQuartet} to which this one is to be checked for equality
     * @return true if the underlying objects of the Triple are considered
     * equal
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MutableQuartet)) {
            return false;
        }
        MutableQuartet<?, ?, ?, ?> p = (MutableQuartet<?, ?, ?, ?>) o;
        return objectsEqual(p.first, first)
                && objectsEqual(p.second, second)
                && objectsEqual(p.third, third)
                && objectsEqual(p.fourth, fourth);
    }

    private static boolean objectsEqual(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the MutableQuartet
     */
    @Override
    public int hashCode() {
        return (first == null ? 0 : first.hashCode())
                ^ (second == null ? 0 : second.hashCode())
                ^ (third == null ? 0 : third.hashCode())
                ^ (fourth == null ? 0 : fourth.hashCode());
    }

    @Override
    public String toString() {
        return "MutableQuartet{"
                + String.valueOf(first) + " "
                + String.valueOf(second) + " "
                + String.valueOf(third) + " "
                + String.valueOf(fourth) + "}";
    }

    /**
     * Convenience method for creating an appropriately typed pair.
     *
     * @param a the first object in the MutableQuartet
     * @param b the second object in the MutableQuartet
     * @param c the third object in the MutableQuartet
     * @param d the fourth object in the MutableQuartet
     * @return a MutableQuartet that is templatized with the types of a, b, c and d
     */
    public static <A, B, C, D> MutableQuartet<A, B, C, D> create(A a, B b, C c, D d) {
        return new MutableQuartet<>(a, b, c, d);
    }
}
