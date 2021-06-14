package ru.destinyman;

import ru.destinyman.utils.ErrorText;
import ru.destinyman.utils.menu.EMenuActions;
import ru.destinyman.utils.menu.MenuActions;

import java.util.ArrayList;

public class Main {

    static boolean checkKey(String arg, String shortKey, String longKey){
       return arg.equals(shortKey) || arg.equals(longKey);
    }

    static void printHelp(String[] args){
        ArrayList<EMenuActions> help = new ArrayList<>();
        help.add(EMenuActions.HELP);
        MenuActions.executeActions(help, args);
    }

    public static void main(String[] args) {

        ArrayList<EMenuActions> menuActions = MenuActions.getActionFromKeys(args);
        switch (args.length) {
            case 0: {
               printHelp(args);
            }
            case 1: {
                if (args[0].startsWith("-") && !(checkKey(args[0], "-h", "--help"))){
                    throw new Error(ErrorText.NO_FILEPATH.getMessage());
                }
                if (checkKey(args[0], "-h", "--help")){
                    MenuActions.executeActions(menuActions, args);
                }
                if (checkKey(args[0], "-a", "--all")){
                    MenuActions.executeActions(menuActions, args);
                }
                //TODO добавить случай для указания только пути до файла
                break;
            }
            default: {
                if (args[args.length - 1].startsWith("-")){
                    printHelp(args);
                }
                if (!args[0].startsWith("-")) {
                    printHelp(args);
                }

                MenuActions.executeActions(menuActions, args);
                break;
            }
        }
    }
}
