package ru.destinyman;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0){
            System.out.println("USAGE: \n " +
                    "-q, --queries-only - generate only gql-queries\n" +
                    "-h, --help - get current usage info\n" +
                    "-a, --all - generate graphql, protofiles and gql-queries\n" +
                    "-qn, --no-query - generate graphql, protofiles WITHOUT gql-queries"
            );
            System.exit(0);
        }
    }
}
