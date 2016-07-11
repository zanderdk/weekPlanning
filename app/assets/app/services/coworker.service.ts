import { Injectable, Inject } from "@angular/core"
import { Http, Response } from "@angular/http"
import { Headers, RequestOptions } from "@angular/http"
import { Coworker } from "./coworkerClasses"


@Injectable()
export class CoworkerService {
    
    private updateCoworkerUrl = "/updateCoworker"
    private addCoworkerUrl = "/coworkersAdd"
    private getCoworkerUrl = "/coworkers"
    private deleteCoworkerUrl = "/deleteCoworker"
    
    constructor(@Inject(Http) private http:Http) { }
    
    public getCoworkers(projectId: number): Promise<Coworker[]> {
        return this.http.get(this.getCoworkerUrl + "?id=" + projectId).toPromise()
            .then(this.extractData)
    }
    
    public deleteCoworker(projectId: number, name: string): Promise<string> {
        return this.http.get(this.deleteCoworkerUrl + "?id=" + projectId + "&name=" + name)
            .toPromise().then(res => res.text())
    }
    
    public addCoworker(projectId: number, name: string): Promise<string> {
        return this.http.get(this.addCoworkerUrl + "?id=" + projectId + "&name=" + name).toPromise()
            .then(res => res.text())
    }
    
    public updateCoworker(projectId: number, old:string, name:string): Promise<string> {
        return this.http.get(this.updateCoworkerUrl + "?id=" + projectId + "&oldName=" + old + "&name=" + name)
            .toPromise().then(res => res.text())
    }
    
    private extractData(res: Response) {
        let body = res.json()
        let data = body || { }
        return data
    }
}