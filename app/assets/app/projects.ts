import {Component} from "angular2/core"

@Component({
    selector: "projects",
    templateUrl: "assets/app/projects.html"
})

export default class ProjectComponent {
    title = "";
    constructor() {
        this.title = "pik"
    }
}
