import {Component, OnInit, Inject} from "@angular/core"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import { ScheduleService } from "../services/schedule.service"
import {Week} from "../services/scheduleClasses"

@Component({
    selector: "editWeek",
    templateUrl: "assets/app/schedule/editWeek.html",
    directives: [ROUTER_DIRECTIVES]
})

export default class EditWeekComponent implements OnInit {
    private sub: any
    private error: string = ""
    private edit: boolean = false
    private week: Week = new Week(0, 0, 0, 0, [], false)
    
    private check(res: string) {
       if(res !== "ok") {
           this.error = res
       } else {
           let link = ['/schedule/' + this.week.projectId];
           this.router.navigate(link); 
       }
    }

    constructor (
        @Inject(ScheduleService) private scheduleService: ScheduleService,
        @Inject(Router) private router: Router,
        @Inject(ActivatedRoute) private route: ActivatedRoute) { }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            let id = +params['projectId']
            let weekId = +params['weekId']
            this.edit = (weekId === 0)? false : true
            this.week.id = weekId
            this.week.projectId = id
            if(this.edit) {
                this.scheduleService.getWeek(this.week.projectId, this.week.id).then(w => {
                    this.week = w
                })
            }
        })
    }
    
    cancel() {
        this.check("ok")
    }

    save(){
        if(!this.edit) {
            this.scheduleService.addWeek(this.week).then(res => this.check(res))
        }
        else {
            this.scheduleService.updateWeek(this.week).then(res => this.check(res))
        }
    }

    delete(){
        this.scheduleService.deleteWeek(this.week).then(res => this.check(res))
    }

}