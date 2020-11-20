//Parser class .. validation for command , return the main command with it's arguments if it has any
class Parser {

    String[] args = new String[10]; //will be filled by arguments extracted by parse method
    String cmd; //will be filled by the command extracted by parse method

    public boolean parse(String input) {

        if (input.contains(" ")) {

            String command = input.substring(0, input.indexOf(" ")); //getting the first word

            if( input.contains(">") || input.contains(">>") || input.contains("|")){ //case :check if the input have redirect or pipe operator

                if(input.contains("|")){

                    String firstCommand = input.substring(0,input.indexOf("|")); //getting the first part
                    String[] firstCommandArgs = firstCommand.split(" "); //pushing in string array

                    try {
                        if(firstCommandArgs[0] != null){ //the command name
                            args[0] = firstCommandArgs[0];
                        }
                        if (firstCommandArgs[1] != null){ //first command arg 1
                            args[1] = firstCommandArgs[1];
                        }
                        if (firstCommandArgs[2] !=null){ //first command arg 2
                            args[2] = firstCommandArgs[2];
                        }
                    }catch (Exception ex){
                        //Exception
                    }

                    String secondCommand = input.substring(input.indexOf("|"), input.length()); //getting the first part
                    String[] secondCommandArgs = secondCommand.split(" "); //pushing in string array

                    try {
                        if(secondCommandArgs[1] != null){ //the command name
                            args[3] = secondCommandArgs[1];
                        }
                        if (secondCommandArgs[2] != null){ //first command arg 1
                            args[4] = secondCommandArgs[2];
                        }
                        if (secondCommandArgs[3] !=null){ //first command arg 2
                            args[5] = secondCommandArgs[3];
                        }
                    }catch (Exception ex){
                        //Exception
                    }

                    cmd = "|";
                    return true;

                }else if((input.contains(">") || input.contains(">>")) && !command.equals("cat") && ( command.equals("help")  || command.equals("ls") || command.equals("pwd") || command.equals("date") || command.equals("more"))) {

                    String[] arguments = input.split(" "); //getting the second word which is the argument

                    args[0] = command; //the first command
                    cmd = arguments[1]; // ">, >>"
                    args[1] = arguments[2]; //the second command

                }

                return true;

            } else if (command.equals("mkdir") || command.equals("rmdir") || command.equals("rm") || command.equals("args") || command.equals("cat") || command.equals("cd") || command.equals("cp") || command.equals("mv") || command.equals("more")) {

                String[] arguments = input.split(" "); //getting the arguments

                for (int i = 0; i < arguments.length; i++) {

                    try {
                        args[i] = arguments[i + 1];
                    } catch (Exception ex) {
                        args[i] = null;
                    }

                }

                cmd = command;

                return true;
            } else {
                return false;
            }
        }else {
            if (input.equals("help") || input.equals("ls") || input.equals("pwd") || input.equals("date") || input.equals("exit") || input.equals("clear")){ //case : commands that don't have arguments

                cmd = input;
                return true;

            }else {
                return false;
            }
        }
    }

    public String getCmd(){
        return cmd;
    }

    public String[] getArgs(){
        return args;
    }

}
