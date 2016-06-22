import {Component, OnInit} from "angular2/core"
import {ProjectService} from "./services/project.service"
import {ProjectWrapper} from "./services/projectClasses";
import {ProjectFilter} from "./projectFilter";

@Component({
    selector: "projects",
    templateUrl: "assets/app/projects.html",
    pipes: [ProjectFilter],
    bindings: [ProjectService]
})

export default class ProjectListComponent implements OnInit {
    private projects: ProjectWrapper[] = []
    private name: string = ""

    private projectService: ProjectService
    constructor ($projectService: ProjectService) { 
        this.projectService = $projectService
    }

    ngOnInit() { this.getProjects() }

    getProjects() {
        this.projectService.getProjects()
                   .then(
                     projects => {
                         this.projects = projects 
                     })
    }
}
