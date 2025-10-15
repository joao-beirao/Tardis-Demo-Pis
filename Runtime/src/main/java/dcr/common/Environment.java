package dcr.common;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An Environment data structure for keeping track of {@link Binding bindings} and managing scopes.
 *
 * @param <V>
 *         the type of values mapped by the Environment.
 */
public final class Environment<V> {
    private final Environment<V> ancestor;
    private final Map<String, Binding> bindings;
    private final int level;

    // This method does not perform any precondition checks on its arguments.
    private Environment(Environment<V> ancestor, int level) {
        this.level = level;
        this.ancestor = ancestor;
        this.bindings = new HashMap<>();
    }

    // Ancillary method for validating identifier arguments
    private static String checkId(String identifier) {
        if (Objects.requireNonNull(identifier).isBlank()) {
            throw new IllegalArgumentException("Cannot bind a value to a null identifier");
        }
        return identifier;
    }

    /**
     * Creates an empty Environment.
     *
     * @param <V>
     *         type of value stored by this Environment
     * @return and empty Environment.
     */
    public static <V> Environment<V> empty() {
        return new Environment<>(null, 0);
    }


    // Ancillary method to recursively traverse environment levels "upwards" until the specified
    // level is reached, starting from this one, adding the binding at that level if one
    // did not previously exist.
    //
    // This method does not perform any precondition checks on its arguments.
    private Optional<Binding> recursiveBindIfAbsent(String identifier, V value, int level) {
        return level == this.level
                ? Optional.ofNullable(
                bindings.putIfAbsent(identifier, new Binding(identifier, value)))
                : ancestor.recursiveBindIfAbsent(identifier, value, level);
    }

    // Ancillary method to recursively traverse environment scopes "upwards", starting from this
    // one, until the first binding with the specified identifier is found, or the most outer
    // scope is reached and no such binding is found.
    //
    // This method does not do any precondition checks on its arguments.
    private Optional<Binding> recursiveLookup(String identifier) {
        return Optional.ofNullable(bindings.get(identifier))
                .or(() -> ancestor == null ? Optional.empty() : ancestor.lookup(identifier));
    }


    /**
     * Creates and returns a new (empty) nested Environment scope.
     *
     * @return a new empty Environment scope, nested within this one.
     */
    public Environment<V> beginScope() {
        return new Environment<V>(this, level + 1);
    }

    /**
     * Creates and returns a new nested scope with an initial binding.
     *
     * @param identifier
     *         key for this binding
     * @param value
     *         the mapped value
     * @return a new nested Environment with the initial binding
     */
    public Environment<V> beginScope(String identifier, V value) {
        Environment<V> newScope = beginScope();
        newScope.bindIfAbsent(identifier, value);
        return newScope;
    }

    /**
     * Returns this Environment's outer scope.
     *
     * @return an {@link Optional} describing the outer scope Environment, or an
     *         {@link Optional#empty() empty Optional} if this Environment already represents the
     *         most outer scope.
     */
    public Optional<Environment<V>> endScope() {
        return Optional.of(ancestor);
    }

    /**
     * Returns the nesting level of this scope. The level increases as scopes are further nested,
     * and the most-outer scope is always assigned the level 0.
     *
     * @return the nesting level of this scope.
     */
    public int getLevel() {
        return level;
    }


    /**
     * Recursively traverse environment levels "upwards", starting from this one, until the first
     * binding with the specified identifier is found, or the most outer scope is reached and no
     * such binding is found
     *
     * @param identifier
     *         key for this binding.
     * @return An {@link Optional} describing the binding associated to this identifier , if one
     *         existed, and an {@link Optional#empty() empty Optional} otherwise.
     */
    public Optional<Binding> lookup(String identifier) {
        return recursiveLookup(checkId(identifier));
    }

    /**
     * Creates a {@link Binding} between the identifier and the value at the current level, if one
     * did not previously exist.
     *
     * @param identifier
     *         key for this binding
     * @param value
     *         the mapped value
     * @return An {@link Optional} describing a previously existing binding, if one existed, and an
     *         {@link Optional#empty() empty Optional} otherwise.
     */
    public Optional<Binding> bindIfAbsent(String identifier, V value) {
        return recursiveBindIfAbsent(checkId(identifier), Objects.requireNonNull(value),
                this.level);
    }

    /**
     * Creates a {@link Binding} between the identifier and the value at the specified level, if one
     * did not previously exist.
     *
     * @param identifier
     *         key for the new binding
     * @param value
     *         the associated value
     * @return An {@link Optional} describing a previously existing binding, if one existed, and an
     *         {@link Optional#empty() empty Optional} otherwise.
     */
    public Optional<Binding> bindIfAbsent(String identifier, V value, int level) {
        if (0 > level || level > this.level) {
            throw new IllegalArgumentException(String.format(
                    "level argument out of bounds (%s): " + "expecting a value between 0 and %s",
                    level, this.level));
        }
        return recursiveBindIfAbsent(checkId(identifier), Objects.requireNonNull(value), level);
    }

    /**
     * <p>Convenience method to look up a {@link Binding} and assign it a new value. The
     * method has no effect if the binding does not exist.
     * </p>
     *
     * <p>The search starts from the current level.
     * </p>
     *
     * @param identifier
     *         key of the previously existing binding to be updated
     * @param newValue
     *         the new value
     * @return An {@link Optional} describing the updated binding, if a previous binding existed,
     *         and {@link Optional#empty() empty Optional} otherwise.
     */
    public Optional<Binding> rebindIfPresent(String identifier, V newValue) {
        return recursiveLookup(checkId(identifier)).map(b -> b.setValue(newValue));
    }


    @Override
    public String toString() {
        String thisLevel = String.format("%s(L%d) %s", "  ".repeat(level), level,
                bindings.entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                binding -> binding.getValue().value())));
        return ancestor == null ? thisLevel : ancestor + "\n" + thisLevel;
    }


    /**
     * A mapping associating an identifier with a value of the type stored by this Environment.
     */
    public final class Binding {
        private final String identifier;
        private V value;

        private Binding(String identifier, V value) {
            this.identifier = identifier;
            this.value = value;
        }

        /**
         * Set a new value for this Binding.
         *
         * @param newValue
         *         the new value
         * @return the updated Binding
         */
        public Binding setValue(V newValue) {
            this.value = Objects.requireNonNull(newValue);
            return this;
        }

        /**
         * Return the value mapped by this Binding.
         *
         * @return the value mapped by this Binding
         */
        public V value() {
            return value;
        }

        /**
         * Returns the nesting level of this Binding.
         *
         * @return the nesting level of this Binding.
         */
        public int level() {
            return getLevel();
        }

        @Override
        public String toString() {
            return String.format("Environment.Binding[value: %s, level: %d]", value, level);
        }
    }

    // TODO [remove test]
    public static void main(String[] args) {
        Environment<String> test = Environment.empty();
        test.bindIfAbsent("k1", "v1");
        test.bindIfAbsent("k2", "v2");
        test = test.beginScope();
        test.bindIfAbsent("k3", "v3");
        test.bindIfAbsent("k4", "v4");
        test = test.beginScope("k5", "v5");
        test.bindIfAbsent("k6", "v6");
        test.bindIfAbsent("k7", "v7");
        System.err.println(test);
        System.err.println(test.lookup("k7"));
        System.err.println(test.lookup("k3"));
        System.err.println(test.bindIfAbsent("k3", "v18"));
        System.err.println(test.bindIfAbsent("k3", "v18", 1));
        System.err.println(test);
        Environment<String>.Binding k1 = test.lookup("k1").get();
        k1.setValue("k21");
        // test.rebind(k1, "k21");
    }
}
