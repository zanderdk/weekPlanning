import { Injectable, Inject } from "@angular/core"
import { Http, Response } from "@angular/http"
import { Headers, RequestOptions } from "@angular/http"
import { WorkType } from "./workTypeClasses"


@Injectable()
export class WorkTypeService {
    
    private addWorkTypeUrl = "/addWorkType"
    private deleteWorkTypeUrl = "/deleteWorkType"
    private updateWorkTypeUrl = "/updateWorkType"
    private getWorkTypesUrl = "/getWorkTypes"
    private getWorkTypeUrl = "/getWorkType"
    
    public deleteWorkType(id: number): Promise<string> {
        return this.http.get(this.deleteWorkTypeUrl + "?id=" + id).toPromise()
            .then(res => res.text())
    }
    
    public addWorkType(work: WorkType): Promise<string> {
        let json: string = JSON.stringify(work)
        return this.http.get(this.addWorkTypeUrl + "?json=" + json).toPromise()
            .then(res => res.text())
    }
    
    public updateWorkType(work: WorkType): Promise<string> {
        let json: string = JSON.stringify(work)
        return this.http.get(this.updateWorkTypeUrl + "?json=" + json).toPromise()
            .then(res => res.text())
    }
    
    public getWorkType(id: number): Promise<WorkType> {
        return new Promise((res, rej) => {
            this.http.get(this.getWorkTypeUrl + "?id=" + id).toPromise()
                .then(re => {
                    if(re.text() === "err") {
                        rej("Denne vagt type blev ikke fundet")
                    } else {
                        let r = this.extractData(re)
                        res(r)
                    }
                })
        })
    }
    
    public getWorkTypes(id: number): Promise<WorkType[]> {
        return this.http.get(this.getWorkTypesUrl + "?id=" + id).toPromise()
            .then(this.extractData)
    }
    
    constructor(@Inject(Http) private http:Http) { }
    
    private extractData(res: Response) {
        let body = res.json()
        let data = body || { }
        return data
    }
}