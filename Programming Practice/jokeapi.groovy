**
 *
 *  @author Stępień Rafał
 *
 */

/**
This is a simple application that gets some random jokes from jokeapi.dev.
It sends GET requests into following REST API, prints jokes one by one with 3 seconds pause between each part,
and saves them into separate txt file.
There are two format of jokes - one has a single part, whereas the second consists of two parts.
*/

package zad1;

//create outputfile or point into that
def joke_output = new File('joke_output.txt')

//create a json parser
import groovy.json.JsonSlurper

def jsonSlurper = new JsonSlurper()

//create an information panel
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField

def number_of_jokes = new JTextField(3)
def list_of_jokes = []

def infoPanel = new JPanel()
infoPanel.add(new JLabel("Number of jokes:"))
infoPanel.add(number_of_jokes)
Object[] options = ["Get last jokes and end"]

//run the panel
def input = 0
while(input == 0) {
	input = JOptionPane.showMessageDialog(null, infoPanel, 
        "Type how many jokes you want to retrieve", 
        JOptionPane.INFORMATION_MESSAGE)
    
    try {
        int integerValue = Integer.parseInt(number_of_jokes.getText());
        if (!number_of_jokes.getText().isEmpty()) {
            i = 0
            while (i<integerValue){
                list_of_jokes.add(jsonSlurper.parseText(new URL("https://v2.jokeapi.dev/joke/Any?safe-mode").getText()))
                i = i + 1
            }
        }
    }
    catch(NumberFormatException ex)
    {
        System.out.println("Exception : "+ex);
    }
}

//print all of jokes and save them into separate file one by one, with 3 seconds delay between each part
counter = 1
list_of_jokes.each { joke ->
    println "\n\nJoke number ${-> counter}"

    if(joke.type=="single"){
        print(joke.joke)
        joke_output << joke.joke + "\n\n"
        sleep(3000)
    }

    if(joke.type=="twopart"){
        print(joke.setup)
        sleep(3000)
        print("\n${->joke.delivery}")
        joke_output << joke.setup + "\t" + joke.delivery + "\n\n"
        sleep(3000)
    }

    counter = counter + 1
}