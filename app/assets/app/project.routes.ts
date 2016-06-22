import { provideRouter, RouterConfig } from "@angular/router"
import ProjectsComponent from "./projects.component";
import AddProjectComponent from "./add.project.component";

export const routes: RouterConfig = [
  { path: "projects", component: ProjectsComponent },
  { path: "addProject", component: AddProjectComponent },
  { path: "editProject/:id", component: ProjectsComponent }
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];