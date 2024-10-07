import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Poll} from "../model/poll";

@Injectable({providedIn: 'root'})
export class PollApiService {

  public constructor(private http: HttpClient) {}

  public getActivePoll(): Observable<Poll> {
    return <Observable<Poll>> this.http.get('poll');
  }

  public submitPoll(pollId: number, optionId: number): Observable<Poll> {
    return <Observable<Poll>> this.http.put(`poll/${pollId}/respond/${optionId}`, null);
  }

}
