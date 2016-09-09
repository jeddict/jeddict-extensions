/**
 * Copyright [2016] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.netbeans.jcode.task.progress;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.BG_BLUE;
import static org.netbeans.jcode.console.Console.BG_MAGENTA;
import static org.netbeans.jcode.console.Console.BG_RED;
import static org.netbeans.jcode.console.Console.BLINK;
import static org.netbeans.jcode.console.Console.BOLD;
import static org.netbeans.jcode.console.Console.FG_BLUE;
import static org.netbeans.jcode.console.Console.FG_MAGENTA;
import static org.netbeans.jcode.console.Console.FG_RED;
import static org.netbeans.jcode.console.Console.FG_WHITE;
import static org.netbeans.jcode.console.Console.UNDERLINE;
import org.netbeans.jcode.task.ITaskSupervisor;

/**
 *
 * @author Gaurav Gupta
 */
public class ProgressConsoleHandler implements ProgressHandler {

    private final static int LIMIT = 100;
    private int taskLimit, state = 0;//TODO taskLimit with %
    private final ITaskSupervisor taskSupervisor;
    private final Set<Message> errorMessage = new LinkedHashSet<>();
    private final Set<Message> warningMessage = new LinkedHashSet<>();
    private final Set<Message> infoMessage = new LinkedHashSet<>();

    public ProgressConsoleHandler(ITaskSupervisor taskSupervisor) {
        this.taskSupervisor = taskSupervisor;
        this.taskLimit = LIMIT;
    }

    public ProgressConsoleHandler(ITaskSupervisor taskSupervisor, int taskLimit) {
        this.taskSupervisor = taskSupervisor;
        this.taskLimit = taskLimit;
    }

    @Override
    public void progress(String message) {
        if (++state < taskLimit) {
            taskSupervisor.proceed(1);
        }
        taskSupervisor.log(message, true);
    }

    @Override
    public void append(String message) {
        taskSupervisor.log(message, true);
    }

    @Override
    public void start() {
        taskSupervisor.start(taskLimit);
    }

    @Override
    public void start(int step) {
        taskSupervisor.start(step);
    }

    @Override
    public void progress(String message, int step) {
        taskSupervisor.log(step, message, true);
    }

    @Override
    public void finish() {
        printMessage(MessageType.INFO,BG_BLUE,FG_BLUE, infoMessage);
        printMessage(MessageType.WARNING,BG_MAGENTA,FG_MAGENTA, warningMessage);
        printMessage(MessageType.ERROR,BG_RED, FG_RED, errorMessage);
    }

    @Override
    public void error(String title, String message) {
        errorMessage.add(new Message(title, message));
    }

    @Override
    public void warning(String title, String message) {
        warningMessage.add(new Message(title, message));
    }

    @Override
    public void info(String title, String message) {
        infoMessage.add(new Message(title, message));
    }

    private void printMessage(MessageType messageType, Console bgColor, Console fgColor, Set<Message> messageRepository) {
        String type = Console.wrap(messageType.getHeading(), bgColor, FG_WHITE, BOLD, BLINK);
        if (!messageRepository.isEmpty()) {
            taskSupervisor.log(type, true);
            String previousHelpMessage = null;
            for (Message message : messageRepository) {
                String currentHelpMessage = null;
                taskSupervisor.log(Console.wrap(message.getTitle() + " > \t", fgColor, BOLD, UNDERLINE), false);
                String parsedMessage[] = message.getDescription().split("#");
                taskSupervisor.log(parsedMessage[0], true);
                if(parsedMessage.length>1){
                    currentHelpMessage = parsedMessage[1];
                }
                if (previousHelpMessage != null) { //currentHelpMessage **
                    if (!previousHelpMessage.equals(currentHelpMessage)) {
                        taskSupervisor.log(previousHelpMessage, true);
                        previousHelpMessage = currentHelpMessage;
                    }
                } else if (currentHelpMessage != null) {//previousHelpMessage == null 
                    previousHelpMessage = currentHelpMessage;
                }
            }
            if(previousHelpMessage!=null){
                taskSupervisor.log(previousHelpMessage, true);
            }
        }
    }

    enum MessageType {
        ERROR("Error"), WARNING("Warning"), INFO("Info");
        private final String value;

        private MessageType(String value) {
            this.value = value;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }
        
        public String getHeading() {
            return String.format("\n\t\t##################################### %s #####################################\t\t\n", value);
        }

    }

    class Message {

        private String title;
        private String description;

        public Message(String title, String description) {
            this.title = title;
            this.description = description;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title the title to set
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.title);
            hash = 17 * hash + Objects.hashCode(this.description);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Message other = (Message) obj;
            if (!Objects.equals(this.title, other.title)) {
                return false;
            }
            if (!Objects.equals(this.description, other.description)) {
                return false;
            }
            return true;
        }
        
        
    }

}