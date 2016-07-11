import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "../services/project.service"
import {ProjectWrapper} from "../services/projectClasses"
import {ProjectFilter} from "./projectFilter"
import { ROUTER_DIRECTIVES } from "@angular/router"
import {MenuService} from "../services/menu.service";
import { Router, ActivatedRoute } from "@angular/router"

@Component({
    selector: "projects",
    templateUrl: "assets/app/project/projects.html",
    pipes: [ProjectFilter],
    bindings: [ProjectService],
    directives: [ROUTER_DIRECTIVES]
})

export default class ProjectsComponent implements OnInit {
    private projects: ProjectWrapper[] = []
    private name: string = ""
    private menuService: MenuService

    constructor (
        @Inject(ProjectService) private projectService: ProjectService,
        @Inject(Router) private router: Router
    ) {}

    ngOnInit() { this.getProjects() }

    getProjects() {
        this.projectService.getProjects()
                   .then(
                     projects => {
                         this.projects = projects
                         this.menuService = new MenuService(0, this.router)
                         this.menuService.initDefaults(4)
                     })
    }
}
