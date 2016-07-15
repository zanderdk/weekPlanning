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
    selector: "editDuty",
    templateUrl: "assets/app/schedule/editDuty.html",
    directives: [ROUTER_DIRECTIVES]
})

export default class EditDutyComponent implements OnInit {
    private sub: any
    private error: string = ""
    private projectId: number = 0
    private coworkers: Coworker[] = []
    private workTypes: WorkType[] = []
    private duty: Duty = new Duty(0, 0, 0, 0, null, null)

    initSelect2(data: Any[] ) {
        return $(".js-example-basic-single").select2({
             data: data
             })
        };

    initSelect22(data: Any[] ) {
        return $(".js-example-basic-single2").select2({
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
            let dutyId = +params['dutyId']
            this.projectId = id
            this.duty.id = dutyId



            this.coworkerService.getCoworkers(id).then(res => {
                this.coworkers = res
                let data = this.coworkers.map(x => {
                    return {'id': x.id, 'text': x.name}
                })

                let sel2 = this.initSelect2(data)
                $(".js-example-basic-single").on(
                    'select2:select',
                    (e) => {
                        let id = +e.params.data.id
                        let arr = this.coworkers
                        let coworker = arr.find(x => {
                            return (x.id === id)
                        })
                        this.duty.coworker = coworker
                        this.duty.coworkerId = id
                    })


                this.workTypeService.getWorkTypes(id).then(res => {
                    this.workTypes = res
                    let data = this.workTypes.map(x => {
                        return {'id': x.id, 'text': x.name}
                    })
                    let sel22 = this.initSelect22(data)
                    $(".js-example-basic-single2").on(
                        'select2:select',
                        (e) => {
                            let id = +e.params.data.id
                            let arr = this.workTypes
                            let work = arr.find(x => {
                                return (x.id === id)
                            })
                            this.duty.workType = work
                            this.duty.workTypeId = work.id
                        })

                    this.scheduleService.getDuty(this.projectId, this.duty.id).then(res => {
                        this.duty = res
                        let coworkerId = this.duty.coworker.id
                        let id = this.duty.workType.id
                         sel22.val(id).trigger("change");
                         sel2.val(coworkerId).trigger("change");
                    })
                })

            })


        })
    }

    cancel() {
        this.check("ok")
    }

    delete() {
        this.scheduleService.deleteDuty(this.projectId, this.duty.id)
            .then(res => this.check(res))
    }

    check(res: string) {
        if(res !== "ok") {
            this.error = res
        } else {
           let link = ['/schedule/' + this.projectId];
           this.router.navigate(link);
        }
    }

    save() {
        let duty = this.duty
        this.scheduleService.updateDuty(this.projectId, duty)
            .then(res => this.check(res))
    }


}
