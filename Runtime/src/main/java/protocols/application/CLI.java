package protocols.application;

import dcr.common.Record;
import dcr.common.data.values.IntVal;
import dcr.common.data.values.RecordVal;
import dcr.common.data.values.StringVal;
import dcr.common.data.values.Value;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import protocols.application.requests.InformationFlowException;

import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.StringTokenizer;

public final class CLI {
    private static final Logger logger = LogManager.getLogger(CLI.class);
    // private final DCRProtocol1 dcrProtocol1;
    private final DCRApp dcrProtocol1;

    private static void onHelpCmd() {
        System.out.println("\nHelp");
        System.out.println("---");
        System.out.println("list :\n\tlist enabled events");
        System.out.println("debug :\n\tlist all events in this end-point");
        System.out.println(
                "input <event_label> <input_value> :\n\texecute input event");
        System.out.println(
                "compute <event_label> :\n\t execute computation event");
        System.out.println("quit :\n\texit the application");
        System.out.println("---");
    }

    private static void onShowGraph(String graph) {
        System.out.println("\nGraph");
        System.out.println("---");
        System.out.println(graph);
        System.out.println("---");
    }

    // FIXME - just the enabled events
    private static void onShowEnabledEvents(String graph) {
        System.out.println("\nGraph");
        System.out.println("---");
        System.out.println(graph);
        System.out.println("---");
    }


    // ==============================
    // == Entry points / callbacks
    // ==============================
    CLI(DCRApp dcrProtocol1) {
        this.dcrProtocol1 = dcrProtocol1;
    }

    void init() {
        // readSystemIn();
        new Thread(this::readSystemIn).start();
    }

    void onReceiveEvent() {
        // TODO [print something]
    }


    // ==============================
    // == Internals
    // ==============================

    private void readSystemIn() {
        Scanner in = new Scanner(System.in);
        String line = "";
        while (line != null && !line.equals("quit")) {
            try {
                System.out.println("\n('help' for available commands)");
                System.out.print("        <select command> % ");
                line = in.nextLine();
                if (line == null) {
                    System.exit(1);
                }
                processCommand(line);
            } catch (Exception e) {
                // TODO: Handle event exception in here
                e.printStackTrace();
                logger.error("Error reading command: " + e.getMessage());
            }
        }
        System.exit(0);
    }

    private void processCommand(String command) throws UnknownHostException,
            InterruptedException, InformationFlowException {
        StringTokenizer tokenizer = new StringTokenizer(command);
        if (!tokenizer.hasMoreTokens()) {return;}
        String cmd = tokenizer.nextToken();
        switch (cmd) {
            case "help":
                CLI.onHelpCmd();
                break;
            case "list": {
                var graph = dcrProtocol1.onListEnabledEvents();
                onShowEnabledEvents(graph);
            }
            break;
            case "debug": {
                var graph = dcrProtocol1.onDisplayGraph();
                onShowGraph(graph);
            }
            break;
            case "!": {
                var eventId = tokenizer.nextToken();
                dcrProtocol1.onExecuteComputationEvent(eventId);
            }
            break;
            case "?": {
                // input action
                // TODO error message
                if (!tokenizer.hasMoreTokens()) {
                    System.err.println("Expecting input-event id after '?'");
                    return;
                }
                var eventId = tokenizer.nextToken();
                if (tokenizer.hasMoreTokens()) {
                    var inputString = tokenizer.nextToken();
                    if (tokenizer.hasMoreTokens()) {
                        System.err.println("Unexpected additional argument for input event: " + tokenizer.nextToken());
                        return;
                    }
                    var inputValue = parseInputVal(inputString);
                    dcrProtocol1.onExecuteInputEvent(eventId, inputValue);
                }
                else {
                    dcrProtocol1.onExecuteInputEvent(eventId);
                }
                // var inputString = tokenizer.nextToken();
                // var inputValue = parseInputVal(inputString);
                // dcrProtocol.onExecuteInputEvent(eventId, inputValue);
            }
            break;
            case "quit":
                System.exit(0);
                break;
            default:
                System.out.println("Unknown command: " + cmd);
                break;
        }
    }

    // ========== input value parsing =========================================
    // ========================================================================
    // (!) for the sake of testing and internal demos only - to be replaced by a GUI

    private static Value parseInputVal(String input) {
        input = input.trim();
        // TODO handle empty input values
        if (input.isEmpty()) {
            throw new IllegalArgumentException(
                    "Not implemented: empty input value not allowed at this point");
        }
        if (input.startsWith("{") && input.endsWith("}")) {
          return parseRecordVal(input.substring(1, input.length() - 1).trim());
        }
        if (input.startsWith("'") && input.endsWith("'")) {
            String string_val = input.substring(1, input.length() - 1);
            return StringVal.of(input.substring(1, input.length() - 1));
        }
        // Boolean
        try {
            return IntVal.of(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Unable to parse input value");
        }
    }

    // a:"22"
    // ab:3;c:{a:55;b:"string"}
    // input 1 {a:2;b:'a';c:{d:1;ab:'d'}}
    private static RecordVal parseRecordVal(String content) {
      if (content.isEmpty())
        throw new IllegalArgumentException("Expecting record fields: empty record not        supported");

      var builder = new Record.Builder<Value>();
      String rest = content;
      while (!rest.isEmpty()) {
        var field_end_pos = -1;
        var next_field_pos = -1;

        var field_assign_pos = rest.indexOf(":");

        if (rest.charAt(field_assign_pos + 1) == '{') {
          // record field value follows - use '}' do detect end of record
          field_end_pos = rest.indexOf("}");
          if (field_end_pos < rest.length() - 1 && rest.charAt(field_end_pos + 1) == ';') {
            // one more field after this one
            next_field_pos = field_end_pos;

          } else {
            // this is the last field
            next_field_pos = rest.length();
          }
          builder.addField(parseRecordFieldVal(rest.substring(0, field_end_pos + 1)));
        } else {
          // primitive field value (not record)
          next_field_pos = rest.indexOf(";");
          if (next_field_pos == -1) {
            // this is the last field
            field_end_pos = rest.length();
            next_field_pos = rest.length();
          } else {
            // one more field after this one
            field_end_pos = next_field_pos;
            ++next_field_pos;
          }
          builder.addField(parseRecordFieldVal(rest.substring(0, field_end_pos)));
        }
        rest = rest.substring(next_field_pos);
      }
      return RecordVal.of(builder.build());
    }

    private static Record.Field<Value> parseRecordFieldVal(String field) {
      var split_pos = field.indexOf(":");
      var name = field.substring(0, split_pos);
      var valueAsString = field.substring(split_pos + 1);
      Value val = parseInputVal(valueAsString);
      return new Record.Field<>(name, val);
    }
}
