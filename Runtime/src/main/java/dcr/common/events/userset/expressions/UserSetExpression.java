package dcr.common.events.userset.expressions;

import dcr.common.Environment;
import dcr.common.events.Event;
import dcr.common.data.values.Value;
import dcr.common.events.userset.values.UserSetVal;
import dcr.common.events.userset.values.UserVal;
import org.apache.commons.lang3.tuple.Pair;

/**
     * UserSetExpression provides a common marker interface for expressions describing a set of
     * swarm members.
     * <p>
     * {@link Event events} rely on user-set expressions to describe both <i>active</i> participants
     * (<i>initiators</i>initiators / <i>senders</i>) as well as <i>passive</i> participants
     * (<i>receivers</i>)
     */
public sealed interface UserSetExpression
        permits ReceiverExpr, RoleExpr, InitiatorExpr, SetDiffExpr, SetUnionExpr {
    UserSetVal eval(Environment<Value> valueEnv, Environment<Pair<UserVal, UserVal>> userEnv);
}
