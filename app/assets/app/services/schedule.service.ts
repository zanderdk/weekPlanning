import { Injectable, Inject } from "@angular/core"
import { Http, Response } from "@angular/http"
import { Headers, RequestOptions } from "@angular/http"
import { Week } from "./scheduleClasses"

@Injectable()
export class ScheduleService {
    
    constructor(@Inject(Http) private http:Http) { }
    
    private getWeeksUrl = "/getWeeks"
    private getDaysUrl =  "/getDays"
    private addWeekUrl = "/addWeek"
    private updateWeekUrl = "/updateWeek"
    private getWeekUrl = "/getWeek"
    private deleteWeekUrl = "/deleteWeek"

    public getWeek(projectId: number, weekId:number): Promise<Week> {
        return this.http.get(this.getWeekUrl + "?projectId=" + projectId + "&weekId=" + weekId)
            .toPromise().then(res => {
                let w = this.extractData(res)
                w['days'] = []
                w['expanded'] = false
                return w
            })
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
            .then(this.extractData)
    }
    
    private extractWeeks(res: Response) {
        let body = res.json()
        let data: Any[] = body || {}
        let weeks = data.map(w => {
            w['days'] = []
            w['expanded'] = false
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