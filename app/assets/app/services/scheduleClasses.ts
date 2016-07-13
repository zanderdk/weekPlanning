
export class Day{
    constructor(
       private id:number,
       private weekId: number,
       private weekDay: string,
       private date: string
    ) {}
}

export class Week{
    constructor(
        public id: number,
        public projectId: number,
        public year: number,
        public weekNo: number,
        public days: Day[],
        public expanded: boolean
    ) {}
}