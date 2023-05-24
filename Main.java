import Utils.*;
import Templates.*;
import CLI.*;

import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.handleInput();

        while (!cli.shouldExit) {
            cli.handleInput();
        }
    }
}
