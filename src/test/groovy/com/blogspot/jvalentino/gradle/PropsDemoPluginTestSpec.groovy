package com.blogspot.jvalentino.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

class PropsDemoPluginTestSpec extends Specification {
    Project project
    PropsDemoPlugin plugin

    def setup() {
        project = ProjectBuilder.builder().build()
        plugin = new PropsDemoPlugin()
    }

    void "test plugin"() {
        when:
        plugin.apply(project)

        then:
        project.tasks.getAt(0).toString() == "task ':add'"
    }
}
