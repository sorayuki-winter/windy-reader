package com.wintersky.windyreader.search;

import com.wintersky.windyreader.data.Book;
import com.wintersky.windyreader.data.source.DataSource;
import com.wintersky.windyreader.data.source.Repository;

import java.util.List;

import javax.inject.Inject;

import static com.wintersky.windyreader.util.Constants.WS;

public class SearchPresenter implements SearchContract.Presenter {

    private SearchContract.View mView;

    private Repository mRepository;

    @Inject
    SearchPresenter(Repository mRepository) {
        this.mRepository = mRepository;
    }

    @Override
    public void takeView(SearchContract.View view) {
        mView = view;
    }

    @Override
    public void dropView() {
        mView = null;
    }

    @Override
    public void search(String url, String keyword) {
        mRepository.searchBook(url, keyword, new DataSource.SearchBookCallback() {
            @Override
            public void onBookSearched(List<Book> books) {
                mView.setResult(books);
            }

            @Override
            public void onDataNotAvailable() {
                WS("search fail");
            }
        });
    }
}
