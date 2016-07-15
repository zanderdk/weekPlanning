import {Component, OnInit, Inject} from "@angular/core"
import { ROUTER_DIRECTIVES } from "@angular/router"
import { Router, ActivatedRoute } from "@angular/router"
import { ScheduleService } from "../services/schedule.service"
import {Day, Duty} from "../services/scheduleClasses"
import {Coworker} from "../services/coworkerClasses"
import {CoworkerService} from "../services/coworker.service"
import {WorkType} from "../services/workTypeClasses"
import {WorkTypeService} from "../services/workType.service"

@Component({
    selector: "addDuty",
    templateUrl: "assets/app/schedule/addDuty.html",
    directives: [ROUTER_DIRECTIVES]
})

export default class AddDutyComponent implements OnInit {
    private sub: any
    private error: string = ""
    private projectId: number = 0
    private coworkers: Coworker[] = []
    private workTypes: WorkType[] = []
    private selectedCoworkersIds: number[] = []
    private selectedWorkType: WorkType = null
    private dayId: number

    private selectedCoworkers(): Coworker[] {
        return this.coworkers.filter(x =>
            {
                let arr = this.selectedCoworkersIds
                let nr = arr.indexOf(x.id)
                let bol = (nr !== -1)
                return bol
            })
    }

    initSelect2(data: Any[] ) {
        $(".js-example-basic-single").select2({
             data: data
             })
        };

    initSelect22(data: Any[] ) {
        $(".js-example-basic-single2").select2({
             data: data
             })
        };

    constructor (
        @Inject(ScheduleService) private scheduleService: ScheduleService,
        @Inject(CoworkerService) private coworkerService: CoworkerService,
        @Inject(WorkTypeService) private workTypeService: WorkTypeService,
        @Inject(Router) private router: Router,
        @Inject(ActivatedRoute) private route: ActivatedRoute) { }

    ngOnInit() {
        this.sub = this.route.params.subscribe(params => {
            let id = +params['projectId']
            let dayId = +params['dayId']
            this.projectId = id
            this.dayId = dayId
            this.coworkerService.getCoworkers(id).then(res => {
                this.coworkers = res
                let data = this.coworkers.map(x => {
                    return {'id': x.id, 'text': x.name}
                })
                this.initSelect2(data)
                $(".js-example-basic-single").on(
                    'select2:select',
                    (e) => {
                        let id = e.params.data.id
                        this.selectedCoworkersIds.push(+id)
                    })

                $(".js-example-basic-single").on(
                    'select2:unselect',
                    (e) => {
                        let id = e.params.data.id
                        let arr = this.selectedCoworkersIds
                        this.selectedCoworkersIds = arr.filter(x => x !== +id)
                    })
                })

            this.workTypeService.getWorkTypes(id).then(res => {
                this.workTypes = res
                this.selectedWorkType = res[0]
                let data = this.workTypes.map(x => {
                    return {'id': x.id, 'text': x.name}
                })
                this.initSelect22(data)
                $(".js-example-basic-single2").on(
                    'select2:select',
                    (e) => {
                        let id = +e.params.data.id
                        let arr = this.workTypes
                        let work = arr.find(x => {
                            return (x.id === id)
                        })
                        this.selectedWorkType = work
                    })

            })


        })
    }

    cancel() {
        this.ch
    }

    check(res: string) {
        if(res !== "ok") {
            this.error = res
        } else {
           let link = ['/schedule/' + this.projectId];
           this.router.navigate(link);
        }
    }

    private dutys(): Duty[] {
        let work = this.selectedWorkType
        let dutys = this.selectedCoworkers().map(x => {
            return new Duty(0, this.dayId, x.id, work.id, x, work)
        })
        return dutys
    }

    save() {
        this.scheduleService.addDutys(this.projectId, this.dutys()).then(res => {
            this.check(res)
        })
    }

}