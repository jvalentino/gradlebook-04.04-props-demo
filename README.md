## 4.4 Gradle Properties

The purpose of this project is to demonstrate that various ways to use Gradle properties, specifically through **project.properties**, and how to handle them in unit testing.

 

#### gradle.properties

Any name-value pair placed in the **gradle.properties** file, within the same directory as the **build.gradle**, will be accessible by that given name. For example:

```
alpha=1
```

This property can be access within Gradle as **project.properties.alpha**.

 

#### Using -P command-line parameters

Name-value pairs prefixed with -P at the command-line, can be accessed as Gradle properties. For example:

```bash
$ gradlew -Pbravo=2 someTask
```

This property can be access within Gradle as **project.properties.bravo**.

 

#### Using -D command-line parameters

The -D option is used at the command-line is used for Java variables accessed via **System.getProperty**(). For these to be access as Gradle properties, they must be prefixed with **org.gradle.project**, for example:

```bash
$ gradlew -Dorg.gradle.project.charlie=3 someTask
```

This property can be accessed within Gradle as **project.properties.charlie**.

 

#### Using environment variables

Environment variables can be picked up was Gradle project properties, as long as they are prefixed with ORG_GRADLE_PROJECT. For example:

 

**Windows**

```bash
$ set ORG_GRADLE_PROJECT_delta=4
```

**Linux**

```bash
$ export ORG_GRADLE_PROJECT_delta=4
```

This property can be accessed within Gradle as **project.properties.delta**.

 

#### src/main/groovy/com/blogspot/jvalentino/gradle/AddPropsTask.groovy

```groovy
class AddPropsTask extends DefaultTask {

    protected AddPropsTask instance = this

    int sum = 0

    @TaskAction
    void perform() {
        Map props = instance.project.properties

        sum = (props.alpha as Integer) + (props.bravo as Integer) +
                (props.charlie as Integer) + (props.delta as Integer)

        println "${props.alpha} + ${props.bravo} + ${props.charlie} " +
                "+ ${props.delta} = ${sum}"
    }
}

```

On Line all properties are accessed via **instance.properties.properties**. The member variable instance is used for later testability. The four properties are summed, and the math is printed to the command0line.

 

#### plugin-tests/local/build.gradle

```groovy
buildscript {
  repositories {
	jcenter()
  }
  dependencies {
    classpath 'com.blogspot.jvalentino.gradle:props-demo:1.0.0'
  }
}

apply plugin: 'props-demo'

```

The build script simply applies the plugin uses for demonstrating Gradle properties.

 

#### plugin-tests/local/gradle.properties

```
alpha=1
```

This file contains the alpha property, to demonstrate a property being declared in **gradle.properties**.

 

#### Manual Testing

```bash
plugin-tests/local$ export ORG_GRADLE_PROJECT_delta=4
plugin-tests/local$ gradlew -Pbravo=2 -Dorg.gradle.project.charlie=3 add --stacktrace

> Task :add 
1 + 2 + 3 + 4 = 10


BUILD SUCCESSFUL

```

The **bravo** property comes from -P, the **charlie** property comes from -D, while the **delta** property comes via an environment variable. Note that on windows the **set** command must be used to set the value of an environment variable. The execution of the task then demonstrated that all the properties were received, and added together.

 

#### src/test/groovy/com/blogspot/jvalentino/gradle/AddPropsTaskTestSpec.groovy

```groovy
class AddPropsTaskTestSpec extends Specification {

    @Subject
    AddPropsTask task
    Project project
    
    def setup() {
        Project p = ProjectBuilder.builder().build()
        task = p.task('add', type:AddPropsTask)
        task.instance = Mock(AddPropsTask)
        project = Mock(ProjectInternal)
    }

```

**Lines 12-14: The member variables**

As the subject of the test, the task class will be accessed by every test case, which is why it is best for it to be a member variable. The Project instance, which will be mocked, will also likely be needed by every test case.

 

**Lines 17-18: Instantiating the task**

The **ProjectBuilder** is required for getting a Project instance, while we are opting to instantiate the task class using **project.task**. The reason for not using **project.apply** to handle constructing via the underlying plugin class, is because **project.properties** cannot be changed in this manner. An easier method for setting properties for testing purposes, it to mock the project so that it can return the properties you need.

 

**Line 19: Mocking the task class**

For internal calls back to the task class itself, specifically to get the **project** instance, we have replaced calls to “this” with the **instance** member variable, that is now a mock.

 

**Line 20: Mocking the project**

To be able to set properties on the project, we must mock the project.

```groovy
    void "test perform"() {
        given:
        Map props = [
            'alpha':'1',
            'bravo':'2',
            'charlie':'3',
            'delta':'4'
        ]
        
        when:
        task.perform()
        
        then:
        1 * task.instance.project >> project
        _ * project.properties >> props
        
        and:
        task.sum == 10
         
    }

```

**Lines 25-30: The mocked properties**

This map is going to be used for the return result of **project.properties**.

 

**Line 36: Using the mock to return a mock**

The expression **task.instance** refers to the mock of the task class, that was handled in the **setup** method. We are telling this task mock to expect a single call to **instance**.**project,** and to return our mock project instance.

 

**Line 37: Return the mock properties**

The expression means that any number of calls are expected to **project.properties**, and each time to return the property map we created in the given clause.



