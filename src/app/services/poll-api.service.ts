import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";

@Injectable({providedIn: 'root'})
export class PollApiService {

  public constructor(private http: HttpClient) {}

  public getActivePoll(): Observable<string> {
    return this.http.get('poll').pipe(map(data => JSON.stringify(data)));
  }

}
