package org.jboss.resteasy.api.validation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.api.validation.ConstraintType.Type;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.validation.ConstraintTypeUtil;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 *          Copyright Mar 6, 2012
 *
 *          {@literal @}TODO Need to work on representation of exceptions
 *          {@literal @}TODO Add javadoc.
 */
public abstract class ResteasyViolationException extends ConstraintViolationException {
    private static final long serialVersionUID = 2623733139912277260L;

    public static final String SUPPRESS_VIOLATION_PATH = "resteasy.validation.suppress.path";

    private volatile List<CloneableMediaType> accept;

    private volatile Exception exception;

    private final List<ResteasyConstraintViolation> propertyViolations = new CopyOnWriteArrayList<>();

    private final List<ResteasyConstraintViolation> classViolations = new CopyOnWriteArrayList<>();

    private final List<ResteasyConstraintViolation> parameterViolations = new CopyOnWriteArrayList<>();

    private final List<ResteasyConstraintViolation> returnValueViolations = new CopyOnWriteArrayList<>();

    private final List<ResteasyConstraintViolation> allViolations = new CopyOnWriteArrayList<>();

    private final List<List<ResteasyConstraintViolation>> violationLists = new CopyOnWriteArrayList<>();

    private transient ConstraintTypeUtil util = getConstraintTypeUtil();

    private boolean suppressPath;

    /**
     * New constructor
     *
     * @param constraintViolations set of constraint violations
     */
    public ResteasyViolationException(final Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(constraintViolations);
        checkSuppressPath();
        accept = new ArrayList<CloneableMediaType>();
        accept.add(CloneableMediaType.TEXT_PLAIN_TYPE);
        convertViolations();
    }

    /**
     * New constructor
     *
     * @param constraintViolations set of constraint violations
     * @param accept               list of accept media types
     */
    public ResteasyViolationException(final Set<? extends ConstraintViolation<?>> constraintViolations,
            final List<MediaType> accept) {
        super(constraintViolations);
        checkSuppressPath();
        this.accept = toCloneableMediaTypeList(accept);
        convertViolations();
    }

    /**
     * New constructor
     *
     * @param container violation container
     */
    public ResteasyViolationException(final SimpleViolationsContainer container) {
        this(container.getViolations());
        setException(container.getException());
    }

    /**
     * New constructor
     *
     * @param container violation container
     * @param accept    list of accept media types
     */

    public ResteasyViolationException(final SimpleViolationsContainer container, final List<MediaType> accept) {
        this(container.getViolations(), accept);
        setException(container.getException());
    }

    public ResteasyViolationException(final String stringRep) {
        super(null);
        checkSuppressPath();
        convertFromString(stringRep);
    }

    public abstract ConstraintTypeUtil getConstraintTypeUtil();

    public List<MediaType> getAccept() {
        return toMediaTypeList(accept);
    }

    public void setAccept(List<MediaType> accept) {
        this.accept = toCloneableMediaTypeList(accept);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
        initCause(exception);
    }

    public List<ResteasyConstraintViolation> getViolations() {
        return allViolations;
    }

    public List<ResteasyConstraintViolation> getPropertyViolations() {
        return propertyViolations;
    }

    public List<ResteasyConstraintViolation> getClassViolations() {
        return classViolations;
    }

    public List<ResteasyConstraintViolation> getParameterViolations() {
        return parameterViolations;
    }

    public List<ResteasyConstraintViolation> getReturnValueViolations() {
        return returnValueViolations;
    }

    public int size() {
        return getViolations().size();
    }

    public List<List<ResteasyConstraintViolation>> getViolationLists() {
        return violationLists;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (List<ResteasyConstraintViolation> violations : violationLists) {
            for (ResteasyConstraintViolation violation : violations) {
                sb.append(violation.toString()).append('\r');
            }
        }

        return sb.toString();
    }

    protected void convertFromString(String stringRep) {
        InputStream is = new ByteArrayInputStream(stringRep.getBytes());
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            line = br.readLine();
            while (line != null) {
                ConstraintType.Type type = ConstraintType.Type.valueOf(line.substring(1, line.length() - 1));
                line = br.readLine();
                String path = line.substring(1, line.length() - 1);
                line = br.readLine();
                String message = line.substring(1, line.length() - 1);
                line = br.readLine();
                String value = line.substring(1, line.length() - 1);
                ResteasyConstraintViolation rcv = new ResteasyConstraintViolation(type, path, message, value);

                switch (type) {
                    case PROPERTY:
                        propertyViolations.add(rcv);
                        break;

                    case CLASS:
                        classViolations.add(rcv);
                        break;

                    case PARAMETER:
                        parameterViolations.add(rcv);
                        break;

                    case RETURN_VALUE:
                        returnValueViolations.add(rcv);
                        break;

                    default:
                        throw new RuntimeException(Messages.MESSAGES.unexpectedViolationType(type));
                }
                allViolations.add(rcv);
                line = br.readLine(); // consume ending '\r'
                line = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(Messages.MESSAGES.unableToParseException());
        }

        violationLists.clear();
        violationLists.add(propertyViolations);
        violationLists.add(classViolations);
        violationLists.add(parameterViolations);
        violationLists.add(returnValueViolations);
    }

    protected int getField(int start, String line) {
        int beginning = line.indexOf('[', start);
        if (beginning == -1) {
            throw new RuntimeException(Messages.MESSAGES.exceptionHasInvalidFormat(line));
        }
        int index = beginning;
        int bracketCount = 1;
        while (++index < line.length()) {
            char c = line.charAt(index);
            if (c == '[') {
                bracketCount++;
            } else if (c == ']') {
                bracketCount--;
            }
            if (bracketCount == 0) {
                break;
            }
        }
        if (bracketCount != 0) {
            throw new RuntimeException(Messages.MESSAGES.exceptionHasInvalidFormat(line));
        }
        return index;
    }

    protected abstract ResteasyConfiguration getResteasyConfiguration();

    protected void checkSuppressPath() {
        ResteasyConfiguration context = getResteasyConfiguration();
        if (context != null) {
            String s = context.getParameter(SUPPRESS_VIOLATION_PATH);
            if (s != null) {
                suppressPath = Boolean.parseBoolean(s);
            }
        }
    }

    protected void convertViolations() {
        if (!violationLists.isEmpty()) {
            return;
        }

        if (getConstraintViolations() != null) {
            for (Iterator<ConstraintViolation<?>> it = getConstraintViolations().iterator(); it.hasNext();) {
                ResteasyConstraintViolation rcv = convertViolation(it.next());
                switch (rcv.getConstraintType()) {
                    case PROPERTY:
                        propertyViolations.add(rcv);
                        break;

                    case CLASS:
                        classViolations.add(rcv);
                        break;

                    case PARAMETER:
                        parameterViolations.add(rcv);
                        break;

                    case RETURN_VALUE:
                        returnValueViolations.add(rcv);
                        break;

                    default:
                        throw new RuntimeException(Messages.MESSAGES.unexpectedViolationType(rcv.getConstraintType()));
                }
                allViolations.add(rcv);
            }
        }

        violationLists.add(propertyViolations);
        violationLists.add(classViolations);
        violationLists.add(parameterViolations);
        violationLists.add(returnValueViolations);
    }

    protected ResteasyConstraintViolation convertViolation(ConstraintViolation<?> violation) {
        Type ct = util.getConstraintType(violation);
        String path = (suppressPath ? "*" : violation.getPropertyPath().toString());
        return new ResteasyConstraintViolation(ct, path, violation.getMessage(),
                convertArrayToString(violation.getInvalidValue()));
    }

    protected static String convertArrayToString(Object o) {
        String result = null;
        if (o instanceof Object[]) {
            Object[] array = Object[].class.cast(o);
            if (array.length == 0) {
                return "[]";
            }
            StringBuffer sb = new StringBuffer("[").append(convertArrayToString(array[0]));
            for (int i = 1; i < array.length; i++) {
                sb.append(", ").append(convertArrayToString(array[i]));
            }
            sb.append("]");
            result = sb.toString();
        } else {
            result = (o == null ? "" : o.toString());
        }
        return result;
    }

    /**
     * It seems that EJB3 wants to clone ResteasyViolationException,
     * and MediaType is not serializable.
     *
     */
    static class CloneableMediaType implements Serializable {
        public static final CloneableMediaType TEXT_PLAIN_TYPE = new CloneableMediaType("plain", "text");

        private static final long serialVersionUID = 9179565449557464429L;

        private String type;

        private String subtype;

        private Map<String, String> parameters;

        CloneableMediaType(final MediaType mediaType) {
            type = mediaType.getType();
            subtype = mediaType.getSubtype();
            parameters = new HashMap<String, String>(mediaType.getParameters());
        }

        CloneableMediaType(final String type, final String subtype) {
            this.type = type;
            this.subtype = subtype;
        }

        public MediaType toMediaType() {
            return new MediaType(type, subtype, parameters);
        }
    }

    protected static List<CloneableMediaType> toCloneableMediaTypeList(List<MediaType> list) {
        List<CloneableMediaType> cloneableList = new ArrayList<CloneableMediaType>();
        for (Iterator<MediaType> it = list.iterator(); it.hasNext();) {
            cloneableList.add(new CloneableMediaType(it.next()));
        }
        return cloneableList;
    }

    protected static List<MediaType> toMediaTypeList(List<CloneableMediaType> cloneableList) {
        List<MediaType> list = new ArrayList<MediaType>();
        for (Iterator<CloneableMediaType> it = cloneableList.iterator(); it.hasNext();) {
            CloneableMediaType cmt = it.next();
            list.add(new MediaType(cmt.type, cmt.subtype, cmt.parameters));
        }
        return list;
    }
}
