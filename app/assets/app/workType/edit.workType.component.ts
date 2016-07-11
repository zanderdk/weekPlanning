import {Component, OnInit, Inject} from "@angular/core"
import {ProjectService} from "../services/project.service"
import {UserService} from "../services/user.service"
import {MenuService} from "../services/menu.service"
import {ProjectFilter} from "./projectFilter"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import {WorkType} from "../services/workTypeClasses"
import {WorkTypeService} from "../services/workType.service"

@Component({
    selector: "editCoworker",
    templateUrl: "assets/app/workType/editWorkType.html",
    directives: [ROUTER_DIRECTIVES]
})

export default class EditWorkTypeComponent implements OnInit {
    private projectId: number = 0
    private visability: string = ""
    private sub: any
    private work: WorkType = new WorkType(0, 0, "", 0.0)
    private menuService: MenuService
    private error: string = ""
    private coworkers: Coworker[] = []
    private edit: boolean = false
    private initName: string = ""
    
    private check(res: string) {
       if(res !== "ok") {
           this.error = res
       } else {
           let link = ['/workTypes/' + this.projectId];
           this.router.navigate(link); 
       }
    }

    constructor (
        @Inject(WorkTypeService) private workTypeService: WorkTypeService,
        @Inject(UserService) private userService: UserService,
        @Inject(Router) private router: Router,
        @Inject(ActivatedRoute) private route: ActivatedRoute) { }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            let id = +params['projectId']
            let workId = (params['workId'] === undefined)? 0 : +params['workId']
            this.work.id = workId
            this.edit = (workId === 0)? false : true
            this.projectId = id
            this.work.projectId = id
            this.userService.getUsersVisabilityForProject(this.projectId)
                .then(res => {
                    this.viability = res
                })
            if(this.edit) {
                this.workTypeService.getWorkType(workId).then(wo => {
                    this.work = wo
                }).catch(res => {
                    this.error = res
                })
            }
        })
    }
    
    save() {
        if(!this.edit) {
            this.workTypeService.addWorkType(this.work).then(res => this.check(res))
        } else {
            this.workTypeService.updateWorkType(this.work).then(res => this.check(res))
        }
    }
    
    delete() {
        this.workTypeService.deleteWorkType(this.work.id).then(res => this.check(res))
    }
    
    cancel() {
        this.check("ok")
    }


}