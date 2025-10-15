package dcr.common.relations;

public interface ControlFlowRelation
        extends Relation {
    enum Type {
        CONDITION("-->*"), MILESTONE("--><>"), RESPONSE("*-->"), INCLUDE("-->+"), EXCLUDE("-->%");
        private final String stringVal;

        private Type(String stringVal) {
            this.stringVal = stringVal;
        }

        @Override
        public String toString() {
            return stringVal;
        }

        public String unparse() {
            return "Type{" + stringVal + '}';
        }

    }


    String targetId();

    Type relationType();
}
