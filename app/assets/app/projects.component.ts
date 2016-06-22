///<reference path="../../../target/web/public/main/lib/angular__core/index.d.ts"/>
import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "./services/project.service"
import {ProjectWrapper} from "./services/projectClasses"
import {ProjectFilter} from "./projectFilter"
import { ROUTER_DIRECTIVES } from "angular2/router"

@Component({
    selector: "projects",
    templateUrl: "assets/app/projects.html",
    pipes: [ProjectFilter],
    bindings: [ProjectService]
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
