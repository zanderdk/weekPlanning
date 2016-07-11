import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "../services/project.service"
import {UserService} from "../services/user.service"
import {MenuService} from "../services/menu.service"
import {ProjectFilter} from "./projectFilter"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import {WorkType} from "../services/workTypeClasses"
import {WorkTypeService} from "../services/workType.service"
import {WorkTypeFilter} from "./workTypeFilter"

@Component({
    selector: "workTypes",
    templateUrl: "assets/app/workType/workTypes.html",
    pipes: [WorkTypeFilter],
    bindings: [WorkTypeService],
    directives: [ROUTER_DIRECTIVES]
})

export default class WorkTypesComponent implements OnInit {
    private projectId: number = 0
    private visability: string = ""
    private sub: any
    private name: string = ""
    private menuService: MenuService
    private workTypes: WorkType[] = []
    
    constructor (
        @Inject(UserService) private userService: UserService,
        @Inject(WorkTypeService) private workTypeService: WorkTypeService,
        @Inject(Router) private router: Router,
        @Inject(ActivatedRoute) private route: ActivatedRoute) { }
    
    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            this.projectId = +params['id'] // (+) converts string 'id' to a number
            this.userService.getUsersVisabilityForProject(this.projectId)
                .then(res => {
                    this.viability = res
                    this.menuService = new MenuService(this.projectId, this.router)
                    this.menuService.initDefaults(2)
                    this.workTypeService.getWorkTypes(this.projectId)
                        .then(works => this.workTypes = works)
                })
        })

    }
    
}