import { Injectable, Inject } from "@angular/core"
import { Http, Response } from "@angular/http"
import { Headers, RequestOptions } from "@angular/http"
import { Week, Day, Duty } from "./scheduleClasses"

@Injectable()
export class ScheduleService {
    
    constructor(@Inject(Http) private http:Http) { }
    
    private getWeeksUrl = "/getWeeks"
    private getDaysUrl =  "/getDays"
    private addWeekUrl = "/addWeek"
    private updateWeekUrl = "/updateWeek"
    private getWeekUrl = "/getWeek"
    private deleteWeekUrl = "/deleteWeek"
    private addDutysUrl = "/addDutys"
    private getDutyUrl = "/getDuty"
    private updateDutyUrl = "/updateDuty"
    private deleteDutyUrl = "/deleteDuty"

    public getWeek(projectId: number, weekId:number): Promise<Week> {
        return this.http.get(this.getWeekUrl + "?projectId=" + projectId + "&weekId=" + weekId)
            .toPromise().then(res => {
                let w = this.extractData(res)
                w['days'] = []
                w['expanded'] = false
                return w
            })
    }

    public addDutys(projectId: number, dutys: Duty[]): Promise<string> {
        let json = JSON.stringify(dutys)
        return this.http.get(this.addDutysUrl + "?projectId=" + projectId + "&json=" + json)
            .toPromise().then(res => res.text())
    }

    public deleteWeek(week: Week): Promise<string> {
        return this.http.get(this.deleteWeekUrl + "?projectId=" + week.projectId + "&weekId=" + week.id)
            .toPromise().then(res => res.text())
    }

    public updateWeek(week: Week): Promise<string> {
        let json = JSON.stringify(week)
        return this.http.get(this.updateWeekUrl + "?json=" + json).toPromise()
            .then(res => res.text())
    }

    public updateDuty(projectId: number, duty: Duty): Promise<Duty> {
        let json = JSON.stringify(duty)
        return this.http.get(this.updateDutyUrl + "?projectId=" + projectId + "&json=" + json).toPromise()
            .then(res => res.text())
    }

    public deleteDuty(projectId: number, dutyId: number): Promise<string> {
        return this.http.get(this.deleteDutyUrl + "?projectId=" + projectId + "&dutyId=" + dutyId)
            .toPromise().then(res => res.text())
    }
    
    public getWeeks(projectId: number): Promise<Week[]> {
       return this.http.get(this.getWeeksUrl + "?projectId=" + projectId).toPromise()
           .then(this.extractWeeks)
    }
    
    public addWeek(week: Week): Promise<string> {
        return this.http.get(this.addWeekUrl 
            + "?projectId=" + week.projectId 
            + "&year=" + week.year 
            + "&weekNo=" + week.weekNo).toPromise()
            .then(res => res.text())
    }
    
    public getDays(projectId: number, weekId: number): Promise<Day[]> {
        return this.http.get(this.getDaysUrl + "?projectId=" + projectId + "&weekId=" + weekId)
            .toPromise()
            .then(this.extractDays)
    }

    private extractDays(res: Response) {
        let body = res.json()
        let data: Any[] = body || {}
        let days = data.map(d => {
            d['expanded'] = false
            let dd = new Day(d.id, d.weekId, d.weekDay, d.date, d.dutys, d.expanded)
            return dd
        })
        return days
    }

    public getDuty(projectId:number, dutyId:number): Promise<Duty> {
        return this.http.get(this.getDutyUrl + "?projectId=" + projectId + "&dutyId=" + dutyId)
            .toPromise().then(this.extractData)
    }
    
    private extractWeeks(res: Response) {
        let body = res.json()
        let data: Any[] = body || {}
        let weeks = data.map(w => {
            w['days'] = []
            w['expanded'] = false
            w['marked'] = false
            return w
        })
        return weeks
    }
    
    private extractData(res: Response) {
        let body = res.json()
        let data = body || { }
        return data
    }
}