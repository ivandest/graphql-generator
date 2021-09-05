package ru.destinyman;

import ru.destinyman.utils.ErrorText;
import ru.destinyman.utils.menu.EMenuActions;
import ru.destinyman.utils.menu.MenuActions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    static boolean checkKey(String arg, String shortKey, String longKey){
       return arg.equals(shortKey) || arg.equals(longKey);
    }

    static void printHelp(List<String> args){
        ArrayList<EMenuActions> help = new ArrayList<>();
        help.add(EMenuActions.HELP);
        MenuActions.executeActions(help, args);
    }

    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        ArrayList<EMenuActions> menuActions = MenuActions.getActionFromKeys(argsList);
        switch (argsList.size()) {
            case 0: {
               printHelp(argsList);
            }
            case 1: {
                if (argsList.get(0).startsWith("-") && !(checkKey(argsList.get(0), "-h", "--help"))){
                    throw new Error(ErrorText.NO_FILEPATH.getMessage());
                }
                if (checkKey(argsList.get(0), "-h", "--help")){
                    MenuActions.executeActions(menuActions, argsList);
                }
                if (checkKey(argsList.get(0), "-a", "--all")){
                    MenuActions.executeActions(menuActions, argsList);
                }
                //TODO добавить случай для указания только пути до файла
                if (argsList.get(0).matches("^(?:[\\w]\\:|\\\\|/)(\\\\[a-z_\\-\\s0-9\\.]+)+\\.(md|csv)$")){
                    MenuActions.executeActions(menuActions, argsList); // временная заглушка
                }
                break;
            }
            default: {
                if (args[args.length - 1].startsWith("-")){
                    printHelp(argsList);
                }
                if (!args[0].startsWith("-")) {
                    printHelp(argsList);
                }

                MenuActions.executeActions(menuActions, argsList);
                break;
            }
        }
    }
}
