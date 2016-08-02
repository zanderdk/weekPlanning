import { Injectable, Inject } from "@angular/core"

@Injectable()
export class MenuService {
    
    constructor(
        private projectId,
        private router: Router) { }

    public removeActive() {
        let bar = $(".navbar-nav")
        bar.find(".active").removeClass('active')
    }
    
    public initDefaults(active: number) {
        $(".navbar-nav").empty();
        this.addToMenu("Projekter", "/projects", active === 4)
        this.addToMenuRightReal("Log ud", "/logout", false)
        
        if(this.projectId !== 0){
            this.addToMenu("Kalender", "/schedule/" + +this.projectId, active === 0)
            this.addToMenu("Medarbejdere", "/coworkers/" + +this.projectId, active === 1)
            this.addToMenu("Vagt typer", "/workTypes/" + +this.projectId, active === 2)
            this.addToMenu("Lokationer", "/locations/" + +this.projectId, active === 3)
        }
    }

    public addToMenuRightReal(name:string, link:string, active:boolean = false) {
        if(active) { this.removeActive() }
        let bar = $("#navbar-nav-right")
        let ht = $("<li class='nav-item'></li>")
        let htd = (!active)? ht : ht.addClass("active")
        let htt = htd.append("<a href='" + link + "' class='nav-link fake-link'>" + name + "</a>")
        bar.append(htt)
    }

    public addToMenuRight(name:string, link:string, active:boolean = false) {
        if(active) { this.removeActive() }
        let bar = $("#navbar-nav-right")
        let ht = $("<li class='nav-item'></li>")
        let htd = (!active)? ht : ht.addClass("active")
        let htt = htd.append("<span class='nav-link fake-link'>" + name + "</span>")
            .on('click', e => {
                let l = [link];
                this.router.navigate(l);
            })
        bar.append(htt)
    }

    public addToMenu(name:string, link:string, active:boolean = false) {
        if(active) { this.removeActive() }
        let bar = $("#navbar-nav")
        let ht = $("<li class='nav-item'></li>")
        let htd = (!active)? ht : ht.addClass("active")
        let htt = htd.append("<span class='nav-link fake-link'>" + name + "</span>")
            .on('click', e => {
                let l = [link];
                this.router.navigate(l);
            })
        bar.append(htt)
    }
    
    
}