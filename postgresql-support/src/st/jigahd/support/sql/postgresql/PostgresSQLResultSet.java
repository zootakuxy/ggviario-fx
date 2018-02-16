package st.jigahd.support.sql.postgresql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PostgresSQLResultSet extends PostgresSQLResult {

    private final int numberColumns;
    private List<PostgresSQLRow> fethsRow;
    private boolean recordCursorPint;
    private int currentPoint = 0;
    private PostgresSQLRow currentRow;

    public PostgresSQLResultSet(ResultSet resultSet) throws SQLException {
        super(resultSet);
        this.numberColumns = this.resultSet.getMetaData().getColumnCount();
        this.fethsRow = new LinkedList<>( );
    }

    public void setRecordCursorPint(boolean recordCursorPint) {
        this.recordCursorPint = recordCursorPint;
    }

    public boolean isRecordCursorPint() {
        return recordCursorPint;
    }


    public PostgresSQLRow nextRow()  {
        try {
            this.resultSet.next();
            if( recordCursorPint && currentPoint <= fethsRow.size() && currentPoint > 0 ){
                this.currentRow = this.fethsRow.get( currentPoint++ -1);
                return currentRow;
            }
            currentRow = new PostgresSQLRow( this.headerMap );
            for( int i =0; i<this.numberColumns; i++  ){
                currentRow.set( i, valueOf( i, headerMapReverse.get( i ), headerTypes.get( i ) ) );
            }

            if( this.recordCursorPint ){
                this.fethsRow.add( currentRow );
            }

            this.currentPoint++;
            return currentRow;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PostgresSQLRow previewRow() {
        if( ! isRecordCursorPint() ) return null;
        if( this.fethsRow.size() < 1 ) return  null;
        this.currentRow = this.fethsRow.get( --currentPoint -1 );
        return currentRow;
    }

    /**
     * @param position base 0
     * @return
     */
    public PostgresSQLRow getCurrentRow( int position ) {
        if( !isRecordCursorPint() ) return  null;
        this.currentRow = fethsRow.get( position );
        this.currentPoint = position +1;
        return currentRow;
    }

    public List<PostgresSQLRow> getFethsRow() {
        return Collections.unmodifiableList( this.fethsRow );
    }

    public PostgresSQLRow currentRow(){
        return this.currentRow;
    }
}
