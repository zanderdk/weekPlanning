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
        $(".navbar-nav li").empty();
        this.addToMenu("Projekter", "/projects", active === 4)
        
        if(this.projectId !== 0){
            this.addToMenu("Kalender", "/schedule/" + +this.projectId, active === 0)
            this.addToMenu("Medarbejdere", "/coworkers/" + +this.projectId, active === 1)
            this.addToMenu("Vagt typer", "/workTypes/" + +this.projectId, active === 2)           
        } 
    }

    public addToMenu(name:string, link:string, active:boolean = false) {
        if(active) { this.removeActive() }
        let bar = $(".navbar-nav")
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