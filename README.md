# Automaton

## what is the purpose of this project?

this project provides a command line interface for automating common tasks.
you can think about it like a very simple subset of ansible, but fully distributed by nature.

it uses a "playbook" to run the task.

you have 3 sections in a playbook:
- nodes
- variables
- stages

`nodes` describe the agents to connect for executing the playbooks

`variables` is the list of all the variables that are used by the playbook (locals to every agent running the playbook)

`stages` is the list of actions that the playbook needs to run.

you also need to give a name to your playbook.

this is a very simple example of a playbook:

    name: test playbook
    nodes:
      port: 8071
      hostnames:
        - localhost
    variables:
      - name: "v1"
        value: "hello world"
    stages:
      - action: "Debug"
        display: "display contents of v1"
        parameters:
          message: "{{v1}}"

## the 'nodes' section

this section describes how to connect to the agents in charge of running the playbook sent by the master process.

you will find here the `port` parameter, which is the port on which every agent is listening, and a list of hostnames

## the 'variables' section

the `variables` section defines the list of the variables that can be used inside the playbook.

to declare a variable, you only need a name and a value.

variables can be used by the action, you just need to use the following syntax: `{{variable name}}`

a variable does not have type, but needs to be declared as string on the yaml file, even if it is a number.

if you don't want to set an initial value for a variable, just use the empty string like this: ""

you can also set the value of a variable dynamically, but we will talk about that on the next section.

## the 'stages' section

the `stages` section is a list of actions.

you have several actions available, this will be explained later.

this is how you define an action:

    - action: "`action name`"
      display: "`action message`"
      condition: "`optional condition`"
      loop:
        index: varname
        range: upperbound
      foreach:
        - v1
        - v2
        - v3
      parameters:
        `action parameter name`: "`action parameter value`"

as you can see, it's quite simple.

- _action name_: you will have to select here the action you want to execute. see later the table with all the available actions to know which one to choose.
- _action message_: displayed before the action starts, and it's a good point to explain here what you action is actually going to do.
- _condition_ is optional. if you have one, it is often associated with a test on a variable. if the condition is false, the action will be skipped. here, you can't use very complex conditions, see the next section for more details.
- _loop_ is optional. if you have one, the stage will be repeated. a variable will be created to host the value of the iteration (it's the _index_)
- _foreach_ is optional. if you have one, the stage will be repeated. a special variable _item_ will host the values that you are listing under foreach, and the value change at every iteration.
- _parameters list_ is a list of key / value. depends on the action you are going to execute.

_a point about conditions_:

conditions are meant to test a value of a variable, and can't really be complex.

the following conditions are supported:

- {{var}} == {{var}} or value _will test if a variable is equals to another variable or a value_
- {{var}} != {{var}} or value _will test if a variable is NOT equals to another variable or a value_
- {{var}} > {{var}} or value _will test if a variable is greater than another variable or a value_
- {{var}} >= {{var}} or value _will test if a variable is greater than or equals to another variable or a value_
- {{var}} < {{var}} or value _will test if a variable is lower than another variable or a value_
- {{var}} <= {{var}} or value _will test if a variable is lower than or equals to another variable or a value_
- exists({{var}} or value) _will be true if the file located at the path hosted by var or value exists_
- readable({{var}} or value) _will be true if the file located at the path hosted by var or value exists and is readable_
- writable({{var}} or value) _will be true if the file located at the path hosted by var or value exists and is writable_

operators like _or_, _and_, _not_ are not allowed. composed conditions (with parenthesis) are not allowed. Keep it simple.

## List of available actions

for now, this is the list of the actions that you can use in your playbooks.

| action | parameters list | description |
| --- | --- | --- |
| Archive | directory, path and action | action can only have the following value: _compress_ or _uncompress_. this action deals with the .tar or .tar.gz archives. as the name implied it, you will use this action to compress a directory or uncompress an archive to a directory. path parameter is the path of the archive. |
| CopyFile | from and to | well, as you can expect, this action copy a file from a destination to a target. you have to give the complete path for both parameters (source and target) |
| Debug | message or file | this action simply display the message or the content of a file. |
| DownloadFile | url and path | as you can expect, this action download a file from the _url_ to the path that you give. |
| Eval | expression and result | evaluate a *numeric* expression and store the result to a variable |
| Execute | command | execute, locally, the command passed as parameter. use sh for unix system and cmd for windows. |
| File | - | all purpose action for dealing with file and directory, will be presented on a special chapter. |
| Import | playbook | with this action, you can import another playbook and run it from your current playbook. this could be used to separate the variable section and the stages section, for example. please note that this action is the only one running on the master process, all the other actions will runs on the agent side. |
| Lines | file, regexp | use this action to change the content of a file. more on this on the next section. |
| SendFile | source, sourcepath, target, port, targetpath | if the playbook is running on the source agent, will send the file located at sourcepath to the agent running on the node target:port, and copy the file on the target path on this node. |
| Template | path and template or body | path is the destination path of the file to be generated. template is the source file of the template. you can also use _body_ parameter and embed the template inside your playbook. of course, all the variable defined in the playbook are accessible inside the template. |
| Unzip | archive and destination | this action unzip the archive passed as parameter to the destination folder. |
| Zip | path and archive | and this action will zip the content of the _path_ directory to create a new archive. |

as you can see it's quite easy to use.

now let's dig inside some of the most interesting actions...

### Eval action

this action allows to process variables and to store the result in one of the processed variables or another one.

let's take an example. you have 2 variables that are actually numbers: v1 with a value of 20 and v2 with a value of 30.

if you enter this action:

    - action: "Eval"
      display: "add v1 to v2 and store in variable v3"
      parameters:
        expression: "{{v1}} + {{v2}}"
        result: "v3"

then you will have 50 as value for the v3 variable.

you can also use this to increment the value of a variable, like this:

    - action: "Eval"
      display: "add 10 to v1"
      parameters:
        expression: "{{v1}} + 10"
        result: "v1"

the expression is whatever expression that can be use in javascript, since it's actually the inner javascript engine associated with java that is used to evaluate the expression.

_please note that if you want to act on a string variable you will have to double-quote the value of the variable !!!_

### File action

the File action actually allows the following actions:

- create or delete a file or a directory
- change the ownership of a file or a directory
- change the permissions of a file or a directory

#### create a directory

use the following parameters to create a directory:

    - action: "File"
      display: "create new test directory"
      parameters:
        path: "{{v1}}/tst"
        state: directory

this is creating a new sub-directory 'tst' inside a path which is the value of the v1 variable.

as you can see, the variables can be used with any parameters.

state in this case is directory. if you just want to create an empty file, you can use the state _file_ instead. in this case it will be the equivalent of the command 'touch'.

#### erase a directory and its contents

to erase the directory created with the previous action, you have to use the following parameters:

    - action: "File"
      display: "delete test directory"
      parameters:
        path: "{{v1}}/tst"
        state: "absent"

can you see the difference? here you change the state from _directory_ to _absent_ and the directory will be removed, as well as its contents.

if it is a file instead of a directory, then only the file will be removed.

#### ownership and permissions

now, look at the following example:

    - action: "File"
      display: "make xl command executable"
      parameters:
        path: "{{xld_dir}}/migration/bin/xl"
        state: "file"
        mode: "755"
        owner: "{{xld_owner}}"

with this command, you are killing two birds with the same stone:

- change ownership of the file
- change permissions of the file to make it executable

if you are using a state of _directory_ instead file, you can also act on a directory. but please note that it will be only the directory, not the content of the directory.

### Lines action

with this action, you can change the content of a text file, by identifying the lines with a regexp.

if you look at the sample playbook, you can see several examples of how to use the *Lines* action.

#### adding a line before every lines matching a regexp

it's easy, use something like this:

    - action: "Lines"
      display: "add a new line to temp.txt"
      parameters:
        file: "./newtest/tst/temp.txt"
        regexp: "and as you know already"
        before: "nice, isn't it?"

- the _file_ parameter is of course the file to update
- the _regexp_ parameter allows to identify the lines
- and the _before_ parameter contains the line to add before every line matching the regexp

#### adding a line after every lines matching a regexp

very similar to the previous one, except that you have to use the parameter *after* instead of _before_

    - action: "Lines"
     display: "add another line to temp.txt"
     parameters:
       file: "./newtest/tst/temp.txt"
       regexp: "enter.+this"
       after: "this is another line."

ah, of course the regexp allows to use... a regexp. it must be a valid java regexp.

see [this link](https://java-regex-tester.appspot.com/) if you are not sure how to create a proper java regexp.

#### replacing all the lines matching a regexp

this time, you will have to use the *replace* parameter, like this:

    - action: "Lines"
      display: "and replace again by never"
      parameters:
        file: "./newtest/tst/temp.txt"
        regexp: "again"
        replace: "never"

### conclusion

you will have a lot to discover with the actions, but the best is to try and check the results.

you have a complete playbook sample in this repository, that you can access from [here](playbook.yaml)

have a look, and feel free to get inspired by it.

## how to build it?

this is a **java native project**, which means that you have to prepare your environment in order to be able to build it.

### install SDKMAN

First thing to do is to install SDKMan with this command:

    curl -s "https://get.sdkman.io" | bash

Then, follow the on-screen instructions to complete the installation. Please note that you might need the zip and unzip packages installed to complete the installation process.

Next, open a new terminal or run:

    source "$HOME/.sdkman/bin/sdkman-init.sh"

Lastly, run the following command to ensure the installation worked. If all went well, the version should be displayed:

    sdk version

To see all the available commands, use the help command:

    sdk help

### install GraalVM

Starts by listing all the JVM available with this command:

    sdk list java

Search for the latest version of GraalVM for the JDK 11. At the time I write this README, it is **21.2.0.r11**

    sdk install java 21.2.0.r11-grl

By default, SDKMAN will update your default configuration to use the new JVM. Check it by running the command:

    java -version

you should see the GraalVM line at the end, like this:

    openjdk version "11.0.12" 2021-07-20
    OpenJDK Runtime Environment GraalVM CE 21.2.0 (build 11.0.12+6-jvmci-21.2-b08)
    OpenJDK 64-Bit Server VM GraalVM CE 21.2.0 (build 11.0.12+6-jvmci-21.2-b08, mixed mode, sharing)

If needed, you will have to change `JAVA_HOME` environment variable to point to GraalVM's distribution. Don't forget to change it also under your IDE! Even more if you are using an embedded version of maven.

Last, let's install the native-image compiler provided by the installed GraalVM's distribution:

    gu install native-image

Then check that everything is installed properly:

    gu list

Should display something like:

    ComponentId              Version             Component name                Stability                     Origin
    ---------------------------------------------------------------------------------------------------------------------------------
    graalvm                  21.2.0              GraalVM Core                  -                             
    js                       21.2.0              Graal.js                      Supported                     
    native-image             21.2.0              Native Image                  Early adopter                 github.com

Good, we can continue.

### Build & Run

We are ready to build our native image by providing the native profile in the Maven package command:

    mvn -Pnative -DskipTests clean package

The Maven command will create the `automaton` executor file in the target folder. So, we can run our app by simply accessing the executor file:

    automaton/target/automaton

And that's it! now you have your native automaton command line, that you can distribute everywhere you need.

To run the agent, use the following command:

    automaton --agent --port=8071 --output=./automaton-agent

To run the master (and send the playbook to the agents), use the following command:

    automaton --playbook=<path to your playbook>
