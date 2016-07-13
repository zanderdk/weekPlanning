import {Component, OnInit, Inject} from "@angular/core"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import { ScheduleService } from "../services/schedule.service"
import {Day} from "../services/scheduleClasses"

@Component({
    selector: "editWeek",
    templateUrl: "assets/app/schedule/editDuty.html",
    directives: [ROUTER_DIRECTIVES]
})

export default class EditDutyComponent implements OnInit {
    private sub: any
    private error: string = ""
    private edit: boolean = false

    constructor (
        @Inject(ScheduleService) private scheduleService: ScheduleService,
        @Inject(Router) private router: Router,
        @Inject(ActivatedRoute) private route: ActivatedRoute) { }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            let id = +params['projectId']
            let weekId = +params['weekId']
            let dayId = +params['dayId']
            this.edit = (weekId === 0)? false : true
        })
    }

}