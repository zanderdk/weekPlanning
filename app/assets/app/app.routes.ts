import { provideRouter, RouterConfig } from "@angular/router"
import ProjectsComponent from "./project/projects.component"
import EditProjectComponent from "./project/edit.project.component"
import ScheduleComponent from "./schedule/schedule.component"
import CoworkersComponent from "./coworkers/coworkers.component"
import EditCoworkersComponent from "./coworkers/edit.coworker.component";
import WorkTypesComponent from "./workType/workTypes.component"
import EditWorkTypeComponent from "./workType/edit.workType.component"
import EditWeekComponent from "./schedule/editWeek.component"
import AddDutyComponent from "./schedule/addDuty.component"
import AddDutyWeekComponent from "./schedule/addDutyWeek.component"
import EditDutyComponent from "./schedule/editDuty.component"
import LocationComponent from "./location/locations.component"
import EditLocationComponent from "./location/edit.location"

export const routes: RouterConfig = [
  { path: "", component: ProjectsComponent },
  { path: "projects", component: ProjectsComponent },
  { path: "editProject/:id", component: EditProjectComponent },
  { path: "schedule/:id", component: ScheduleComponent },
  { path: "editCoworker/:id/:name", component: EditCoworkersComponent },
  { path: "editCoworker/:id", component: EditCoworkersComponent },
  { path: "coworkers/:id", component: CoworkersComponent },
  { path: "workTypes/:id", component: WorkTypesComponent },
  { path: "editWorkType/:projectId", component: EditWorkTypeComponent },
  { path: "editWorkType/:projectId/:workId", component: EditWorkTypeComponent },
  { path: "addDuty/:projectId/:dayId", component: AddDutyComponent },
  { path: "addDutyWeek/:projectId/:weekId", component: AddDutyWeekComponent },
  { path: "editDuty/:projectId/:dutyId", component: EditDutyComponent },
  { path: "editWeek/:projectId/:weekId", component: EditWeekComponent },
  { path: "locations/:id", component: LocationComponent },
  { path: "editLocation/:projectId/:locationId", component: EditLocationComponent }
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];