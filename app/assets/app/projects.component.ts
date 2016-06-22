import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "./services/project.service"
import {ProjectWrapper} from "./services/projectClasses"
import {ProjectFilter} from "./projectFilter"
import { ROUTER_DIRECTIVES } from "@angular/router"

@Component({
    selector: "projects",
    templateUrl: "assets/app/projects.html",
    pipes: [ProjectFilter],
    bindings: [ProjectService],
    directives: [ROUTER_DIRECTIVES]
})

export default class ProjectsComponent implements OnInit {
    private projects: ProjectWrapper[] = []
    private name: string = ""

    private projectService: ProjectService
    constructor (@Inject(ProjectService) $projectService: ProjectService) {
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
