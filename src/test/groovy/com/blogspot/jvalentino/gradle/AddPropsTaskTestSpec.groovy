package com.blogspot.jvalentino.gradle

import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification
import spock.lang.Subject

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
}
