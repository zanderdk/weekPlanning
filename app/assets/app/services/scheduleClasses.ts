import Coworker from "./coworkerClasses"
import WorkType from "./workTypeClasses"
import Location from "./locationClasses"

export class Day{
    constructor(
       public id:number,
       public weekId: number,
       public weekDay: string,
       public date: string,
       public dutys: Duty[],
       public expanded: boolean
    ) {}

    public color(): string {
        return (this.dutys.length === 0)? "danger" : "success"
    }
}

export class Duty{
    constructor(
        public id: number,
        public dayId: number,
        public coworkerId: number,
        public workTypeId: number,
        public locationId: number,
        public coworker: Coworker,
        public workType: WorkType,
        public location: Location
    ) { }

}

export class Week{
    constructor(
        public id: number,
        public projectId: number,
        public year: number,
        public weekNo: number,
        public days: Day[],
        public expanded: boolean,
        public marked: boolean
    ) {}
}